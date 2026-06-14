import SwiftUI

struct RegisterView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var appState: AppState

    @State private var name = ""
    @State private var email = ""
    @State private var password = ""
    @State private var confirmPassword = ""

    private var canSubmit: Bool {
        !name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty &&
        !email.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty &&
        password.count >= 6 &&
        confirmPassword == password
    }

    var body: some View {
        Form {
            Section("Thong tin") {
                TextField("Ten hien thi", text: $name)
                TextField("Email", text: $email)
                    .textInputAutocapitalization(.never)
                    .keyboardType(.emailAddress)
                    .autocorrectionDisabled()
                SecureField("Password", text: $password)
                SecureField("Nhap lai password", text: $confirmPassword)
            }

            Section {
                Button {
                    Task {
                        await appState.signUp(
                            name: name.trimmingCharacters(in: .whitespacesAndNewlines),
                            email: email.trimmingCharacters(in: .whitespacesAndNewlines),
                            password: password
                        )
                        if appState.session != nil {
                            dismiss()
                        }
                    }
                } label: {
                    if appState.isBusy {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                    } else {
                        Text("Tao tai khoan")
                            .frame(maxWidth: .infinity)
                    }
                }
                .disabled(appState.isBusy || !canSubmit)
            }
        }
        .navigationTitle("Dang ky")
        .toolbar {
            ToolbarItem(placement: .cancellationAction) {
                Button("Dong") {
                    dismiss()
                }
            }
        }
    }
}
