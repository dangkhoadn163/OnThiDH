import SwiftUI

struct LoginView: View {
    @EnvironmentObject private var appState: AppState

    @State private var email = ""
    @State private var password = ""
    @State private var showsRegister = false

    var body: some View {
        Form {
            Section("Dang nhap") {
                TextField("Email", text: $email)
                    .textInputAutocapitalization(.never)
                    .keyboardType(.emailAddress)
                    .autocorrectionDisabled()

                SecureField("Password", text: $password)

                Button {
                    Task {
                        await appState.signIn(
                            email: email.trimmingCharacters(in: .whitespacesAndNewlines),
                            password: password
                        )
                    }
                } label: {
                    if appState.isBusy {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                    } else {
                        Text("Login")
                            .frame(maxWidth: .infinity)
                    }
                }
                .disabled(appState.isBusy || email.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || password.isEmpty)

                Button("Quen mat khau") {
                    Task {
                        await appState.sendPasswordReset(email: email.trimmingCharacters(in: .whitespacesAndNewlines))
                    }
                }
                .disabled(appState.isBusy || email.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
            }

            Section("Tai khoan moi") {
                Button("Dang ky") {
                    showsRegister = true
                }
                .disabled(appState.isBusy)
            }
        }
        .sheet(isPresented: $showsRegister) {
            NavigationStack {
                RegisterView()
            }
        }
    }
}
