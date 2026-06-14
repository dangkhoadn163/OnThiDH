import Foundation

actor FirebaseDatabaseClient {
    private let decoder = JSONDecoder()
    private let encoder = JSONEncoder()

    func fetchProfile(uid: String) async throws -> UserProfile? {
        try await get(path: "account/\(uid).json")
    }

    func fetchSubjects() async throws -> [SubjectDefinition] {
        let availableKeys: [String: Bool]? = try await get(path: "monhoc.json", queryItems: [URLQueryItem(name: "shallow", value: "true")])
        let keys = Set(availableKeys?.keys.map { $0 } ?? [])

        return SubjectDefinition.displayNames.keys
            .filter { keys.contains($0) }
            .sorted {
                subjectTitle(for: $0) < subjectTitle(for: $1)
            }
            .map { SubjectDefinition(id: $0, title: subjectTitle(for: $0)) }
    }

    func fetchExamSections(uid: String, subjectID: String) async throws -> ExamSectionData {
        let examPayloads: [String: RemoteExamPayload] = try await get(path: "monhoc/\(subjectID).json") ?? [:]
        let completedRecords: [String: CompletedExamRecord] = try await get(path: "account/\(uid)/\(subjectID)/de.json") ?? [:]

        let validCompleted = completedRecords.filter { _, record in
            record.nametest?.isEmpty == false &&
            !(record.dapandalam?.answer?.isEmpty ?? true) &&
            !(record.dapandalam?.score?.isEmpty ?? true)
        }

        let exams = examPayloads.map { key, payload in
            RemoteExam(
                id: key,
                text: payload.text?.trimmingCharacters(in: .whitespacesAndNewlines).nilIfEmpty ?? key,
                answer: payload.answer,
                testPages: makePages(from: payload.test ?? [:]),
                detailResultPages: makePages(from: payload.detailresult ?? [:])
            )
        }
        .sorted { $0.text.localizedCaseInsensitiveCompare($1.text) == .orderedAscending }

        let completed = exams.compactMap { exam -> ExamListItem? in
            guard let record = validCompleted[exam.id] else {
                return nil
            }

            return ExamListItem(
                id: exam.id,
                title: record.nametest ?? exam.text,
                exam: exam,
                completedRecord: record
            )
        }

        let pending = exams.compactMap { exam -> ExamListItem? in
            guard validCompleted[exam.id] == nil else {
                return nil
            }

            return ExamListItem(
                id: exam.id,
                title: exam.text,
                exam: exam,
                completedRecord: nil
            )
        }

        return ExamSectionData(
            availableCount: exams.count,
            completed: completed,
            pending: pending
        )
    }

    func fetchCompletedLibrary(uid: String) async throws -> [CompletedLibraryItem] {
        let subjects = try await fetchSubjects()
        var library: [CompletedLibraryItem] = []

        for subject in subjects {
            let section = try await fetchExamSections(uid: uid, subjectID: subject.id)
            for item in section.completed {
                library.append(CompletedLibraryItem(subject: subject, item: item))
            }
        }

        return library.sorted { lhs, rhs in
            if lhs.subject.title != rhs.subject.title {
                return lhs.subject.title.localizedCaseInsensitiveCompare(rhs.subject.title) == .orderedAscending
            }
            return lhs.item.title.localizedCaseInsensitiveCompare(rhs.item.title) == .orderedAscending
        }
    }

    func saveProfile(session: AuthSession, profile: UserProfile) async throws {
        let payload = UserProfilePayload(
            name: profile.name,
            email: profile.email,
            avatar: profile.avatar,
            school: profile.school,
            className: profile.className,
            address: profile.address,
            phone: profile.phone,
            grade: profile.grade,
            birth: profile.birth
        )
        try await patch(path: "account/\(session.localID).json", body: payload, authToken: session.idToken)
    }

    func saveExamResult(
        session: AuthSession,
        subjectID: String,
        examID: String,
        examTitle: String,
        answerString: String,
        scoreString: String
    ) async throws {
        let payload = ExamResultPayload(
            dapandalam: CompletedAnswer(answer: answerString, score: scoreString),
            nametest: examTitle
        )
        try await patch(
            path: "account/\(session.localID)/\(subjectID)/de/\(examID).json",
            body: payload,
            authToken: session.idToken
        )
    }

    private func makePages(from rawPages: [String: String]) -> [ExamPage] {
        rawPages.compactMap { key, value in
            guard let url = URL(string: value) else {
                return nil
            }

            return ExamPage(id: key, title: key, url: url)
        }
        .sorted { lhs, rhs in
            pageSortKey(lhs.id) < pageSortKey(rhs.id)
        }
    }

    private func pageSortKey(_ raw: String) -> (Int, Int, String) {
        let suffix = raw.replacingOccurrences(of: "page", with: "")
        let parts = suffix.split(separator: "_", omittingEmptySubsequences: false)
        let major = Int(parts.first ?? "") ?? 0
        let minor = parts.count > 1 ? (Int(parts[1]) ?? 0) : 0
        return (major, minor, raw)
    }

    private func subjectTitle(for key: String) -> String {
        SubjectDefinition.displayNames[key] ?? key
    }

    private func get<T: Decodable>(path: String, queryItems: [URLQueryItem] = []) async throws -> T? {
        let url = try makeURL(path: path, queryItems: queryItems)
        let (data, response) = try await URLSession.shared.data(from: url)
        try validate(response: response)
        if data == Data("null".utf8) {
            return nil
        }
        return try decoder.decode(T.self, from: data)
    }

    private func patch<Body: Encodable>(path: String, body: Body, authToken: String?) async throws {
        let authQuery = authToken.map { [URLQueryItem(name: "auth", value: $0)] } ?? []
        let url = try makeURL(path: path, queryItems: authQuery)
        var request = URLRequest(url: url)
        request.httpMethod = "PATCH"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try encoder.encode(body)

        let (_, response) = try await URLSession.shared.data(for: request)
        try validate(response: response)
    }

    private func validate(response: URLResponse) throws {
        guard let httpResponse = response as? HTTPURLResponse,
              (200..<300).contains(httpResponse.statusCode) else {
            throw FirebaseAuthError.invalidResponse
        }
    }

    private func makeURL(path: String, queryItems: [URLQueryItem] = []) throws -> URL {
        var url = FirebaseConfig.databaseBaseURL
        for segment in path.split(separator: "/") {
            url.appendPathComponent(String(segment))
        }

        guard var components = URLComponents(url: url, resolvingAgainstBaseURL: false) else {
            throw FirebaseAuthError.invalidResponse
        }
        if !queryItems.isEmpty {
            components.queryItems = queryItems
        }
        guard let url = components.url else {
            throw FirebaseAuthError.invalidResponse
        }
        return url
    }
}

private struct ExamResultPayload: Encodable {
    let dapandalam: CompletedAnswer
    let nametest: String
}

private struct UserProfilePayload: Encodable {
    let name: String?
    let email: String?
    let avatar: String?
    let school: String?
    let className: String?
    let address: String?
    let phone: String?
    let grade: String?
    let birth: String?

    enum CodingKeys: String, CodingKey {
        case name
        case email
        case avatar
        case school
        case className = "class"
        case address
        case phone
        case grade
        case birth
    }
}

private extension String {
    var nilIfEmpty: String? {
        isEmpty ? nil : self
    }
}
