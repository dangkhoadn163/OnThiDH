import Foundation

struct AuthSession: Codable, Equatable, Sendable {
    let localID: String
    let email: String
    let idToken: String
    let refreshToken: String?
    let issuedAt: Date?
}

struct UserProfile: Codable, Equatable, Sendable {
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

struct SubjectDefinition: Identifiable, Equatable, Sendable {
    let id: String
    let title: String

    static let displayNames: [String: String] = [
        "anhvan": "Anh van",
        "vatly": "Vat ly",
        "hoahoc": "Hoa hoc",
        "toanhoc": "Toan hoc",
        "sinhhoc": "Sinh hoc",
        "lichsu": "Lich su",
        "dialy": "Dia ly",
        "gdcd": "GDCD",
    ]

    var examRule: ExamRule {
        switch id {
        case "anhvan":
            return ExamRule(questionCount: 50, durationSeconds: 60 * 60, scorePerAnswer: 0.2)
        case "toanhoc":
            return ExamRule(questionCount: 50, durationSeconds: 90 * 60, scorePerAnswer: 0.2)
        default:
            return ExamRule(questionCount: 40, durationSeconds: 50 * 60, scorePerAnswer: 0.25)
        }
    }
}

struct RemoteExam: Identifiable, Sendable {
    let id: String
    let text: String
    let answer: String?
    let testPages: [ExamPage]
    let detailResultPages: [ExamPage]
}

struct ExamPage: Identifiable, Sendable {
    let id: String
    let title: String
    let url: URL
}

struct ExamRule: Sendable {
    let questionCount: Int
    let durationSeconds: Int
    let scorePerAnswer: Double
}

struct CompletedAnswer: Codable, Sendable {
    let answer: String?
    let score: String?
}

struct CompletedExamRecord: Codable, Sendable {
    let dapandalam: CompletedAnswer?
    let nametest: String?
}

struct ExamListItem: Identifiable, Sendable {
    let id: String
    let title: String
    let exam: RemoteExam
    let completedRecord: CompletedExamRecord?
}

struct ExamSectionData: Sendable {
    let availableCount: Int
    let completed: [ExamListItem]
    let pending: [ExamListItem]
}

struct CompletedLibraryItem: Identifiable, Sendable {
    let subject: SubjectDefinition
    let item: ExamListItem

    var id: String {
        "\(subject.id)::\(item.id)"
    }
}

struct RemoteExamPayload: Codable, Sendable {
    let answer: String?
    let test: [String: String]?
    let detailresult: [String: String]?
    let text: String?
}

struct ExamSubmission: Sendable {
    let answerString: String
    let scoreString: String
    let correctCount: Int
}

struct QuestionReview: Identifiable, Sendable {
    enum Status: Sendable {
        case correct
        case incorrect
        case unanswered
    }

    let id: Int
    let questionNumber: Int
    let selectedChoice: AnswerChoice?
    let correctChoice: AnswerChoice?
    let status: Status
}

struct ExamDraft: Codable, Sendable {
    let selectionRawValues: [String?]
    let secondsRemaining: Int
    let savedAt: Date
}

enum AnswerChoice: String, CaseIterable, Identifiable, Sendable {
    case a
    case b
    case c
    case d

    var id: String { rawValue }

    var title: String {
        rawValue.uppercased()
    }
}

@MainActor
enum ExamDraftStore {
    private static let keyPrefix = "OnThiDHiOS.ExamDraft"

    static func load(uid: String, subjectID: String, examID: String) -> ExamDraft? {
        guard let data = UserDefaults.standard.data(forKey: storageKey(uid: uid, subjectID: subjectID, examID: examID)),
              let draft = try? JSONDecoder().decode(ExamDraft.self, from: data) else {
            return nil
        }

        return draft
    }

    static func save(_ draft: ExamDraft, uid: String, subjectID: String, examID: String) {
        guard let data = try? JSONEncoder().encode(draft) else {
            return
        }

        UserDefaults.standard.set(data, forKey: storageKey(uid: uid, subjectID: subjectID, examID: examID))
    }

    static func clear(uid: String, subjectID: String, examID: String) {
        UserDefaults.standard.removeObject(forKey: storageKey(uid: uid, subjectID: subjectID, examID: examID))
    }

    static func hasDraft(uid: String, subjectID: String, examID: String) -> Bool {
        load(uid: uid, subjectID: subjectID, examID: examID) != nil
    }

    private static func storageKey(uid: String, subjectID: String, examID: String) -> String {
        "\(keyPrefix).\(uid).\(subjectID).\(examID)"
    }
}

enum ExamScoring {
    static func answerKey(from raw: String?, expectedQuestionCount: Int) -> [AnswerChoice?] {
        guard let raw, !raw.isEmpty else {
            return Array(repeating: nil, count: expectedQuestionCount)
        }

        var result = Array<AnswerChoice?>(repeating: nil, count: expectedQuestionCount)
        var digits = ""

        for character in raw {
            if character.isNumber {
                digits.append(character)
                continue
            }

            guard ["A", "B", "C", "D"].contains(String(character)),
                  let questionNumber = Int(digits),
                  let choice = AnswerChoice(rawValue: String(character).lowercased()),
                  questionNumber >= 1,
                  questionNumber <= expectedQuestionCount else {
                digits = ""
                continue
            }

            result[questionNumber - 1] = choice
            digits = ""
        }

        return result
    }

    static func submission(
        selections: [AnswerChoice?],
        answerKey: [AnswerChoice?],
        fillMissingAnswers: Bool,
        scorePerAnswer: Double
    ) -> ExamSubmission {
        var tokens: [String] = []
        var correctCount = 0
        let totalQuestions = min(selections.count, answerKey.count)

        for index in 0..<totalQuestions {
            let selection = selections[index]
            if let selection {
                tokens.append("\(index + 1)\(selection.rawValue)")
                if selection == answerKey[index] {
                    correctCount += 1
                }
            } else if fillMissingAnswers {
                tokens.append("\(index + 1)e")
            }
        }

        let score = Double(correctCount) * scorePerAnswer
        return ExamSubmission(
            answerString: tokens.joined(),
            scoreString: formatScore(score),
            correctCount: correctCount
        )
    }

    static func userSelections(from raw: String?, expectedQuestionCount: Int) -> [AnswerChoice?] {
        guard let raw, !raw.isEmpty else {
            return Array(repeating: nil, count: expectedQuestionCount)
        }

        var result = Array<AnswerChoice?>(repeating: nil, count: expectedQuestionCount)
        var digits = ""

        for character in raw {
            if character.isNumber {
                digits.append(character)
                continue
            }

            guard let questionNumber = Int(digits),
                  questionNumber >= 1,
                  questionNumber <= expectedQuestionCount else {
                digits = ""
                continue
            }

            if let choice = AnswerChoice(rawValue: String(character).lowercased()) {
                result[questionNumber - 1] = choice
            } else if character == "e" || character == "E" {
                result[questionNumber - 1] = nil
            }

            digits = ""
        }

        return result
    }

    static func review(
        userAnswerString: String?,
        answerKeyRaw: String?,
        expectedQuestionCount: Int
    ) -> [QuestionReview] {
        let selections = userSelections(from: userAnswerString, expectedQuestionCount: expectedQuestionCount)
        let answerKey = answerKey(from: answerKeyRaw, expectedQuestionCount: expectedQuestionCount)

        return (0..<expectedQuestionCount).map { index in
            let selected = selections[index]
            let correct = answerKey[index]
            let status: QuestionReview.Status

            if selected == nil {
                status = .unanswered
            } else if selected == correct {
                status = .correct
            } else {
                status = .incorrect
            }

            return QuestionReview(
                id: index,
                questionNumber: index + 1,
                selectedChoice: selected,
                correctChoice: correct,
                status: status
            )
        }
    }

    static func formatScore(_ score: Double) -> String {
        let tenths = score * 10
        if tenths.rounded(.toNearestOrAwayFromZero) == tenths {
            return String(format: "%.1f", score)
        }
        return String(format: "%.2f", score)
    }
}
