import SwiftUI

@MainActor
final class AppState: ObservableObject {
    @Published var session: AuthSession?
    @Published var profile: UserProfile?
    @Published var isBusy = false
    @Published var errorMessage: String?
    @Published var infoMessage: String?

    let authClient = FirebaseAuthClient()
    let databaseClient = FirebaseDatabaseClient()
    let storageClient = FirebaseStorageClient()

    private let defaultsKey = "OnThiDHiOS.AuthSession"
    private let tokenRefreshInterval: TimeInterval = 50 * 60

    init() {
        restoreSession()
        if session != nil {
            Task {
                await refreshSessionIfPossible()
                await refreshProfile()
            }
        }
    }

    func signIn(email: String, password: String) async {
        await runBusyTask { [self] in
            let session = try await self.authClient.signIn(email: email, password: password)
            self.session = session
            self.persistSession()
            do {
                self.profile = try await self.databaseClient.fetchProfile(uid: session.localID)
            } catch {
                self.profile = nil
            }
        }
    }

    func signUp(name: String, email: String, password: String) async {
        await runBusyTask { [self] in
            let session = try await self.authClient.signUp(email: email, password: password)
            self.session = session
            self.persistSession()
            self.profile = UserProfile(
                name: name,
                email: email,
                avatar: nil,
                school: nil,
                className: nil,
                address: nil,
                phone: nil,
                grade: nil,
                birth: nil
            )
            do {
                try await self.databaseClient.saveProfile(session: session, profile: self.profile!)
            } catch {
                self.infoMessage = "Tao tai khoan thanh cong, nhung luu profile len Firebase that bai."
            }
        }
    }

    func sendPasswordReset(email: String) async {
        await runBusyTask { [self] in
            try await self.authClient.sendPasswordReset(email: email)
            self.infoMessage = "Da gui email reset password."
        }
    }

    func changePassword(currentPassword: String, newPassword: String) async {
        guard let session else {
            errorMessage = "Khong tim thay phien dang nhap."
            return
        }

        await runBusyTask { [self] in
            let updatedSession = try await self.authClient.changePassword(
                email: session.email,
                currentPassword: currentPassword,
                newPassword: newPassword
            )
            self.session = updatedSession
            self.persistSession()
            self.infoMessage = "Da doi mat khau."
        }
    }

    func signOut() {
        session = nil
        profile = nil
        infoMessage = nil
        errorMessage = nil
        UserDefaults.standard.removeObject(forKey: defaultsKey)
    }

    func refreshProfile() async {
        guard let session else {
            profile = nil
            return
        }

        await refreshSessionIfPossible()

        do {
            profile = try await databaseClient.fetchProfile(uid: session.localID)
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func updateProfile(_ profile: UserProfile) async {
        guard session != nil else {
            errorMessage = "Khong tim thay phien dang nhap."
            return
        }

        await runBusyTask { [self] in
            let session = try await self.authorizedSession()
            try await self.databaseClient.saveProfile(session: session, profile: profile)
            self.profile = profile
            self.infoMessage = "Da cap nhat thong tin tai khoan."
        }
    }

    func uploadAvatar(imageData: Data, contentType: String, fileExtension: String) async {
        guard let session else {
            errorMessage = "Khong tim thay phien dang nhap."
            return
        }

        let currentProfile = profile ?? UserProfile(
            name: nil,
            email: session.email,
            avatar: nil,
            school: nil,
            className: nil,
            address: nil,
            phone: nil,
            grade: nil,
            birth: nil
        )

        await runBusyTask { [self] in
            let session = try await self.authorizedSession()
            let avatarURL = try await self.storageClient.uploadAvatar(
                session: session,
                data: imageData,
                contentType: contentType,
                fileExtension: fileExtension
            )
            let updatedProfile = UserProfile(
                name: currentProfile.name,
                email: currentProfile.email,
                avatar: avatarURL,
                school: currentProfile.school,
                className: currentProfile.className,
                address: currentProfile.address,
                phone: currentProfile.phone,
                grade: currentProfile.grade,
                birth: currentProfile.birth
            )
            try await self.databaseClient.saveProfile(session: session, profile: updatedProfile)
            self.profile = updatedProfile
            self.infoMessage = "Da cap nhat avatar."
        }
    }

    func saveExamResult(
        subjectID: String,
        examID: String,
        examTitle: String,
        answerString: String,
        scoreString: String
    ) async throws {
        let session = try await authorizedSession()
        try await databaseClient.saveExamResult(
            session: session,
            subjectID: subjectID,
            examID: examID,
            examTitle: examTitle,
            answerString: answerString,
            scoreString: scoreString
        )
    }

    private func runBusyTask(_ operation: @escaping () async throws -> Void) async {
        isBusy = true
        errorMessage = nil
        infoMessage = nil

        do {
            try await operation()
        } catch {
            errorMessage = error.localizedDescription
        }

        isBusy = false
    }

    private func persistSession() {
        guard let session else {
            return
        }

        if let data = try? JSONEncoder().encode(session) {
            UserDefaults.standard.set(data, forKey: defaultsKey)
        }
    }

    private func restoreSession() {
        guard let data = UserDefaults.standard.data(forKey: defaultsKey),
              let session = try? JSONDecoder().decode(AuthSession.self, from: data) else {
            return
        }

        self.session = session
    }

    private func authorizedSession() async throws -> AuthSession {
        guard let currentSession = session else {
            throw FirebaseAuthError.remote("SESSION_NOT_FOUND")
        }

        guard shouldRefresh(currentSession) else {
            return currentSession
        }

        let refreshedSession = try await authClient.refreshSession(currentSession)
        session = refreshedSession
        persistSession()
        return refreshedSession
    }

    private func refreshSessionIfPossible() async {
        guard let currentSession = session,
              shouldRefresh(currentSession) else {
            return
        }

        do {
            let refreshedSession = try await authClient.refreshSession(currentSession)
            session = refreshedSession
            persistSession()
        } catch {
            // Keep the old session and let the next protected request surface an error if needed.
        }
    }

    private func shouldRefresh(_ session: AuthSession) -> Bool {
        guard session.refreshToken?.isEmpty == false else {
            return false
        }

        guard let issuedAt = session.issuedAt else {
            return true
        }

        return Date().timeIntervalSince(issuedAt) >= tokenRefreshInterval
    }
}
