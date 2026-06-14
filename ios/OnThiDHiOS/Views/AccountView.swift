import PhotosUI
import SwiftUI
import UIKit

struct AccountView: View {
    @EnvironmentObject private var appState: AppState
    @State private var showsChangePassword = false
    @State private var showsProfileEditor = false
    @State private var headerPhotoPickerItem: PhotosPickerItem?

    var body: some View {
        List {
            Section {
                accountHeader
                    .listRowInsets(EdgeInsets(top: 8, leading: 16, bottom: 8, trailing: 16))
                    .listRowBackground(Color.clear)
            }

            Section("Thong tin") {
                infoRow(title: "Ten", value: appState.profile?.name ?? "Chua co")
                infoRow(title: "Email", value: appState.profile?.email ?? appState.session?.email ?? "Chua co")
                infoRow(title: "Truong", value: appState.profile?.school ?? "Chua co")
                infoRow(title: "Lop", value: appState.profile?.className ?? "Chua co")
                infoRow(title: "Dia chi", value: appState.profile?.address ?? "Chua co")
                infoRow(title: "So dien thoai", value: appState.profile?.phone ?? "Chua co")
                infoRow(title: "Khoi", value: appState.profile?.grade ?? "Chua co")
                infoRow(title: "Ngay sinh", value: appState.profile?.birth ?? "Chua co")
                infoRow(title: "UID", value: appState.session?.localID ?? "Chua co", useMonospaced: true)
            }

            Section("Quan ly") {
                Button("Sua ho so") {
                    showsProfileEditor = true
                }

                Button("Tai lai profile") {
                    Task {
                        await appState.refreshProfile()
                    }
                }

                Button("Doi mat khau") {
                    showsChangePassword = true
                }

                Button("Gui email reset password") {
                    guard let email = appState.session?.email else {
                        return
                    }

                    Task {
                        await appState.sendPasswordReset(email: email)
                    }
                }
            }

            Section("Khac") {
                NavigationLink("Support") {
                    SupportView()
                }

                NavigationLink("Rate") {
                    RateView()
                }
            }

            Section {
                Button("Logout", role: .destructive) {
                    appState.signOut()
                }
            }
        }
        .listStyle(.insetGrouped)
        .navigationTitle("Tai khoan")
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                NavigationMenuButton()
            }
        }
        .sheet(isPresented: $showsChangePassword) {
            NavigationStack {
                ChangePasswordView()
            }
            .environmentObject(appState)
        }
        .sheet(isPresented: $showsProfileEditor) {
            NavigationStack {
                ProfileEditorView(
                    profile: appState.profile ?? UserProfile(
                        name: nil,
                        email: appState.session?.email,
                        avatar: nil,
                        school: nil,
                        className: nil,
                        address: nil,
                        phone: nil,
                        grade: nil,
                        birth: nil
                    ),
                    fallbackEmail: appState.session?.email ?? ""
                )
            }
            .environmentObject(appState)
        }
        .task(id: appState.session?.localID) {
            if appState.profile == nil {
                await appState.refreshProfile()
            }
        }
        .task(id: headerPhotoPickerItem) {
            guard let headerPhotoPickerItem else {
                return
            }
            await uploadPickedPhoto(headerPhotoPickerItem)
            self.headerPhotoPickerItem = nil
        }
    }

    private var accountHeader: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 14) {
                PhotosPicker(selection: $headerPhotoPickerItem, matching: .images) {
                    ZStack(alignment: .bottomTrailing) {
                        ProfileAvatarView(avatarURL: appState.profile?.avatar, size: 64)

                        Image(systemName: "camera.fill")
                            .font(.caption.weight(.bold))
                            .foregroundStyle(.white)
                            .padding(7)
                            .background(Color.indigo, in: Circle())
                            .offset(x: 4, y: 4)
                    }
                }
                .buttonStyle(.plain)
                .disabled(appState.isBusy)

                VStack(alignment: .leading, spacing: 4) {
                    Text(appState.profile?.name ?? "Hoc sinh")
                        .font(.headline)
                    Text(appState.profile?.email ?? appState.session?.email ?? "")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                        .lineLimit(1)
                }
            }

            if let avatar = appState.profile?.avatar,
               !avatar.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                Text("Cham vao avatar de doi anh truc tiep. URL avatar van duoc dong bo qua truong `avatar` tren Firebase.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            } else {
                Text("Cham vao avatar de chon anh tu Photos, hoac vao Sua ho so de dan avatar URL thu cong.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            }
        }
        .padding()
        .background(
            LinearGradient(
                colors: [Color(.secondarySystemBackground), Color.indigo.opacity(0.08)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            ),
            in: RoundedRectangle(cornerRadius: 18)
        )
    }

    private func infoRow(title: String, value: String, useMonospaced: Bool = false) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.caption)
                .foregroundStyle(.secondary)
            Text(value)
                .font(useMonospaced ? .footnote.monospaced() : .body)
                .textSelection(.enabled)
        }
        .padding(.vertical, 2)
    }

    private func uploadPickedPhoto(_ item: PhotosPickerItem) async {
        guard let rawData = try? await item.loadTransferable(type: Data.self),
              let image = UIImage(data: rawData),
              let jpegData = image.jpegData(compressionQuality: 0.82) else {
            appState.errorMessage = "Khong doc duoc anh da chon."
            return
        }

        await appState.uploadAvatar(imageData: jpegData, contentType: "image/jpeg", fileExtension: "jpg")
    }
}

struct ChangePasswordView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var appState: AppState

    @State private var currentPassword = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""

    private var canSubmit: Bool {
        !currentPassword.isEmpty &&
        newPassword.count >= 6 &&
        confirmPassword == newPassword
    }

    var body: some View {
        Form {
            Section("Bao mat") {
                SecureField("Mat khau hien tai", text: $currentPassword)
                SecureField("Mat khau moi", text: $newPassword)
                SecureField("Nhap lai mat khau moi", text: $confirmPassword)
            }

            Section {
                Button {
                    Task {
                        await appState.changePassword(currentPassword: currentPassword, newPassword: newPassword)
                        if appState.errorMessage == nil {
                            dismiss()
                        }
                    }
                } label: {
                    if appState.isBusy {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                    } else {
                        Text("Cap nhat mat khau")
                            .frame(maxWidth: .infinity)
                    }
                }
                .disabled(appState.isBusy || !canSubmit)
            }
        }
        .navigationTitle("Doi mat khau")
        .toolbar {
            ToolbarItem(placement: .cancellationAction) {
                Button("Dong") {
                    dismiss()
                }
            }
        }
    }
}

private struct ProfileEditorView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var appState: AppState

    @State private var photoPickerItem: PhotosPickerItem?
    @State private var name: String
    @State private var email: String
    @State private var avatar: String
    @State private var school: String
    @State private var className: String
    @State private var address: String
    @State private var phone: String
    @State private var grade: String
    @State private var birth: String

    init(profile: UserProfile, fallbackEmail: String) {
        _name = State(initialValue: profile.name ?? "")
        _email = State(initialValue: profile.email ?? fallbackEmail)
        _avatar = State(initialValue: profile.avatar ?? "")
        _school = State(initialValue: profile.school ?? "")
        _className = State(initialValue: profile.className ?? "")
        _address = State(initialValue: profile.address ?? "")
        _phone = State(initialValue: profile.phone ?? "")
        _grade = State(initialValue: profile.grade ?? "")
        _birth = State(initialValue: profile.birth ?? "")
    }

    private var previewAvatarURL: String? {
        avatar.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? nil : avatar.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private var canSave: Bool {
        !name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty &&
        !email.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    var body: some View {
        Form {
            Section("Avatar") {
                HStack(spacing: 14) {
                    ProfileAvatarView(avatarURL: previewAvatarURL, size: 72)

                    VStack(alignment: .leading, spacing: 4) {
                        Text("Dan link anh vao truong Avatar URL de doi anh dai dien.")
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                        Text("Vi du: https://...")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }
                }

                PhotosPicker(selection: $photoPickerItem, matching: .images) {
                    Label("Chon anh tu Photos", systemImage: "photo.badge.plus")
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                .disabled(appState.isBusy)

                TextField("Avatar URL", text: $avatar)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
                    .keyboardType(.URL)
            }

            Section("Thong tin co ban") {
                TextField("Ten", text: $name)
                TextField("Email", text: $email)
                    .disabled(true)
                    .foregroundStyle(.secondary)
                TextField("Truong", text: $school)
                TextField("Lop", text: $className)
                TextField("Khoi", text: $grade)
                TextField("Ngay sinh", text: $birth)
            }

            Section("Lien he") {
                TextField("Dia chi", text: $address, axis: .vertical)
                TextField("So dien thoai", text: $phone)
                    .keyboardType(.phonePad)
            }

            Section {
                Button {
                    Task {
                        await appState.updateProfile(
                            UserProfile(
                                name: trimmed(name),
                                email: trimmed(email),
                                avatar: trimmed(avatar),
                                school: trimmed(school),
                                className: trimmed(className),
                                address: trimmed(address),
                                phone: trimmed(phone),
                                grade: trimmed(grade),
                                birth: trimmed(birth)
                            )
                        )
                        if appState.errorMessage == nil {
                            dismiss()
                        }
                    }
                } label: {
                    if appState.isBusy {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                    } else {
                        Text("Luu thong tin")
                            .frame(maxWidth: .infinity)
                    }
                }
                .disabled(appState.isBusy || !canSave)
            }
        }
        .navigationTitle("Sua ho so")
        .task(id: photoPickerItem) {
            guard let photoPickerItem else {
                return
            }
            await uploadPickedPhoto(photoPickerItem)
            self.photoPickerItem = nil
        }
        .toolbar {
            ToolbarItem(placement: .cancellationAction) {
                Button("Dong") {
                    dismiss()
                }
            }
        }
    }

    private func trimmed(_ value: String) -> String? {
        let result = value.trimmingCharacters(in: .whitespacesAndNewlines)
        return result.isEmpty ? "" : result
    }

    private func uploadPickedPhoto(_ item: PhotosPickerItem) async {
        guard let rawData = try? await item.loadTransferable(type: Data.self),
              let image = UIImage(data: rawData),
              let jpegData = image.jpegData(compressionQuality: 0.82) else {
            appState.errorMessage = "Khong doc duoc anh da chon."
            return
        }

        await appState.uploadAvatar(imageData: jpegData, contentType: "image/jpeg", fileExtension: "jpg")
        if let uploadedURL = appState.profile?.avatar {
            avatar = uploadedURL
        }
    }
}

private struct ProfileAvatarView: View {
    let avatarURL: String?
    let size: CGFloat

    var body: some View {
        ZStack {
            Circle()
                .fill(
                    LinearGradient(
                        colors: [Color.indigo, Color.cyan],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .frame(width: size, height: size)

            if let avatarURL,
               let url = URL(string: avatarURL),
               !avatarURL.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                AsyncImage(url: url) { phase in
                    switch phase {
                    case .empty:
                        ProgressView()
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFill()
                    case .failure:
                        Image(systemName: "person.fill")
                            .font(.system(size: size * 0.32))
                            .foregroundStyle(.white)
                    @unknown default:
                        Image(systemName: "person.fill")
                            .font(.system(size: size * 0.32))
                            .foregroundStyle(.white)
                    }
                }
                .frame(width: size, height: size)
                .clipShape(Circle())
            } else {
                Image(systemName: "person.fill")
                    .font(.system(size: size * 0.32))
                    .foregroundStyle(.white)
            }
        }
    }
}
