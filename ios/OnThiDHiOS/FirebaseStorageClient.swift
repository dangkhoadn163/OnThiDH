import Foundation

actor FirebaseStorageClient {
    private struct StorageObjectResponse: Decodable {
        let name: String
        let bucket: String
        let downloadTokens: String?

        enum CodingKeys: String, CodingKey {
            case name
            case bucket
            case downloadTokens = "downloadTokens"
        }
    }

    private struct StorageErrorEnvelope: Decodable {
        struct StorageError: Decodable {
            let message: String
        }

        let error: StorageError
    }

    func uploadAvatar(session: AuthSession, data: Data, contentType: String, fileExtension: String) async throws -> String {
        let objectName = "avatar_\(session.localID)_\(Int(Date().timeIntervalSince1970 * 1000)).\(fileExtension)"
        var components = URLComponents(string: "https://firebasestorage.googleapis.com/v0/b/\(FirebaseConfig.storageBucket)/o")!
        components.queryItems = [
            URLQueryItem(name: "uploadType", value: "media"),
            URLQueryItem(name: "name", value: objectName),
        ]

        guard let url = components.url else {
            throw FirebaseAuthError.invalidResponse
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(session.idToken)", forHTTPHeaderField: "Authorization")
        request.setValue(contentType, forHTTPHeaderField: "Content-Type")
        request.httpBody = data

        let (responseData, response) = try await URLSession.shared.data(for: request)
        guard let httpResponse = response as? HTTPURLResponse else {
            throw FirebaseAuthError.invalidResponse
        }

        if !(200..<300).contains(httpResponse.statusCode) {
            if let remoteError = try? JSONDecoder().decode(StorageErrorEnvelope.self, from: responseData) {
                throw FirebaseAuthError.remote(remoteError.error.message)
            }
            throw FirebaseAuthError.invalidResponse
        }

        let object = try JSONDecoder().decode(StorageObjectResponse.self, from: responseData)
        let encodedName = encodeObjectName(object.name)

        if let token = object.downloadTokens?.split(separator: ",").first {
            return "https://firebasestorage.googleapis.com/v0/b/\(object.bucket)/o/\(encodedName)?alt=media&token=\(token)"
        }

        return "https://firebasestorage.googleapis.com/v0/b/\(object.bucket)/o/\(encodedName)?alt=media"
    }

    private func encodeObjectName(_ name: String) -> String {
        var allowed = CharacterSet.urlPathAllowed
        allowed.remove(charactersIn: "/")
        return name.addingPercentEncoding(withAllowedCharacters: allowed) ?? name
    }
}
