import Foundation

enum FirebaseAuthError: LocalizedError {
    case invalidResponse
    case remote(String)

    var errorDescription: String? {
        switch self {
        case .invalidResponse:
            return "Khong nhan duoc phan hoi hop le tu Firebase."
        case .remote(let message):
            return message.replacingOccurrences(of: "_", with: " ").capitalized
        }
    }
}

actor FirebaseAuthClient {
    private struct AuthRequest: Codable {
        let email: String
        let password: String
        let returnSecureToken: Bool
    }

    private struct RegisterRequest: Codable {
        let email: String
        let password: String
        let returnSecureToken: Bool
    }

    private struct PasswordResetRequest: Codable {
        let requestType: String
        let email: String
    }

    private struct PasswordUpdateRequest: Codable {
        let idToken: String
        let password: String
        let returnSecureToken: Bool
    }

    private struct AuthResponse: Codable {
        let localId: String
        let email: String
        let idToken: String
        let refreshToken: String?
    }

    private struct RefreshResponse: Codable {
        let userID: String
        let idToken: String
        let refreshToken: String?

        enum CodingKeys: String, CodingKey {
            case userID = "user_id"
            case idToken = "id_token"
            case refreshToken = "refresh_token"
        }
    }

    private struct ErrorEnvelope: Codable {
        struct RemoteError: Codable {
            let message: String
        }

        let error: RemoteError
    }

    func signIn(email: String, password: String) async throws -> AuthSession {
        let endpoint = endpoint(path: "accounts:signInWithPassword")
        let request = AuthRequest(email: email, password: password, returnSecureToken: true)
        let response: AuthResponse = try await postJSON(to: endpoint, body: request)
        return AuthSession(
            localID: response.localId,
            email: response.email,
            idToken: response.idToken,
            refreshToken: response.refreshToken,
            issuedAt: Date()
        )
    }

    func signUp(email: String, password: String) async throws -> AuthSession {
        let endpoint = endpoint(path: "accounts:signUp")
        let request = RegisterRequest(email: email, password: password, returnSecureToken: true)
        let response: AuthResponse = try await postJSON(to: endpoint, body: request)
        return AuthSession(
            localID: response.localId,
            email: response.email,
            idToken: response.idToken,
            refreshToken: response.refreshToken,
            issuedAt: Date()
        )
    }

    func sendPasswordReset(email: String) async throws {
        let endpoint = endpoint(path: "accounts:sendOobCode")
        let request = PasswordResetRequest(requestType: "PASSWORD_RESET", email: email)
        let _: EmptyResponse = try await postJSON(to: endpoint, body: request)
    }

    func refreshSession(_ session: AuthSession) async throws -> AuthSession {
        guard let refreshToken = session.refreshToken,
              !refreshToken.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            throw FirebaseAuthError.remote("MISSING_REFRESH_TOKEN")
        }

        let endpoint = URL(string: "https://securetoken.googleapis.com/v1/token?key=\(FirebaseConfig.apiKey)")!
        let response: RefreshResponse = try await postForm(
            to: endpoint,
            body: [
                "grant_type": "refresh_token",
                "refresh_token": refreshToken,
            ]
        )

        return AuthSession(
            localID: response.userID,
            email: session.email,
            idToken: response.idToken,
            refreshToken: response.refreshToken ?? refreshToken,
            issuedAt: Date()
        )
    }

    func changePassword(email: String, currentPassword: String, newPassword: String) async throws -> AuthSession {
        let currentSession = try await signIn(email: email, password: currentPassword)
        let endpoint = endpoint(path: "accounts:update")
        let request = PasswordUpdateRequest(idToken: currentSession.idToken, password: newPassword, returnSecureToken: true)
        let response: AuthResponse = try await postJSON(to: endpoint, body: request)
        return AuthSession(
            localID: response.localId,
            email: response.email,
            idToken: response.idToken,
            refreshToken: response.refreshToken,
            issuedAt: Date()
        )
    }

    private func endpoint(path: String) -> URL {
        URL(string: "https://identitytoolkit.googleapis.com/v1/\(path)?key=\(FirebaseConfig.apiKey)")!
    }

    private func postJSON<Request: Encodable, Response: Decodable>(to url: URL, body: Request) async throws -> Response {
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try JSONEncoder().encode(body)

        let (data, response) = try await URLSession.shared.data(for: request)
        guard let httpResponse = response as? HTTPURLResponse else {
            throw FirebaseAuthError.invalidResponse
        }

        if (200..<300).contains(httpResponse.statusCode) {
            return try JSONDecoder().decode(Response.self, from: data)
        }

        if let remoteError = try? JSONDecoder().decode(ErrorEnvelope.self, from: data) {
            throw FirebaseAuthError.remote(remoteError.error.message)
        }

        throw FirebaseAuthError.invalidResponse
    }

    private func postForm<Response: Decodable>(to url: URL, body: [String: String]) async throws -> Response {
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/x-www-form-urlencoded; charset=UTF-8", forHTTPHeaderField: "Content-Type")
        request.httpBody = formEncodedData(from: body)

        let (data, response) = try await URLSession.shared.data(for: request)
        guard let httpResponse = response as? HTTPURLResponse else {
            throw FirebaseAuthError.invalidResponse
        }

        if (200..<300).contains(httpResponse.statusCode) {
            return try JSONDecoder().decode(Response.self, from: data)
        }

        if let remoteError = try? JSONDecoder().decode(ErrorEnvelope.self, from: data) {
            throw FirebaseAuthError.remote(remoteError.error.message)
        }

        throw FirebaseAuthError.invalidResponse
    }

    private func formEncodedData(from body: [String: String]) -> Data? {
        var allowed = CharacterSet.urlQueryAllowed
        allowed.remove(charactersIn: "&=+")

        let query = body.map { key, value in
            let encodedKey = key.addingPercentEncoding(withAllowedCharacters: allowed) ?? key
            let encodedValue = value.addingPercentEncoding(withAllowedCharacters: allowed) ?? value
            return "\(encodedKey)=\(encodedValue)"
        }
        .joined(separator: "&")

        return query.data(using: .utf8)
    }
}

private struct EmptyResponse: Codable {}
