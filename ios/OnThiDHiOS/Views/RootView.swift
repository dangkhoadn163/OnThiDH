import SwiftUI

enum RootTab: Hashable {
    case home
    case review
    case account
}

enum RootSheet: String, Identifiable {
    case support
    case rate
    case changePassword

    var id: String { rawValue }
}

@MainActor
final class ShellState: ObservableObject {
    @Published var selectedTab: RootTab = .home
    @Published var activeSheet: RootSheet?
}

struct RootView: View {
    @EnvironmentObject private var appState: AppState
    @StateObject private var shellState = ShellState()

    var body: some View {
        Group {
            if appState.session == nil {
                NavigationStack {
                    LoginView()
                        .navigationTitle("On Thi DH")
                }
            } else {
                TabView(selection: $shellState.selectedTab) {
                    NavigationStack {
                        SubjectListView()
                    }
                    .tabItem {
                        Label("Mon hoc", systemImage: "books.vertical.fill")
                    }
                    .tag(RootTab.home)

                    NavigationStack {
                        ReviewLibraryView()
                    }
                    .tabItem {
                        Label("Da lam", systemImage: "checklist")
                    }
                    .tag(RootTab.review)

                    NavigationStack {
                        AccountView()
                    }
                    .tabItem {
                        Label("Tai khoan", systemImage: "person.crop.circle")
                    }
                    .tag(RootTab.account)
                }
                    .task(id: appState.session?.localID) {
                    await appState.refreshProfile()
                }
                .environmentObject(shellState)
                .sheet(item: $shellState.activeSheet) { sheet in
                    NavigationStack {
                        switch sheet {
                        case .support:
                            SupportView()
                        case .rate:
                            RateView()
                        case .changePassword:
                            ChangePasswordView()
                        }
                    }
                    .environmentObject(appState)
                }
            }
        }
        .alert("Thong bao", isPresented: Binding(
            get: { appState.errorMessage != nil || appState.infoMessage != nil },
            set: { isPresented in
                if !isPresented {
                    appState.errorMessage = nil
                    appState.infoMessage = nil
                }
            }
        )) {
            Button("OK", role: .cancel) {}
        } message: {
            Text(appState.errorMessage ?? appState.infoMessage ?? "")
        }
    }
}

struct NavigationMenuButton: View {
    @EnvironmentObject private var appState: AppState
    @EnvironmentObject private var shellState: ShellState

    var body: some View {
        Menu {
            Button {
                shellState.selectedTab = .home
            } label: {
                Label("Trang chu", systemImage: "house")
            }

            Button {
                shellState.selectedTab = .review
            } label: {
                Label("Xem lai de da lam", systemImage: "checklist")
            }

            Button {
                shellState.selectedTab = .account
            } label: {
                Label("Thong tin tai khoan", systemImage: "person.crop.circle")
            }

            Divider()

            Button {
                shellState.activeSheet = .support
            } label: {
                Label("Tro giup va phan hoi", systemImage: "lifepreserver")
            }

            Button {
                shellState.activeSheet = .rate
            } label: {
                Label("Danh gia", systemImage: "star.bubble")
            }

            Divider()

            Button {
                shellState.activeSheet = .changePassword
            } label: {
                Label("Doi mat khau", systemImage: "lock.rotation")
            }

            Button(role: .destructive) {
                appState.signOut()
            } label: {
                Label("Dang xuat", systemImage: "rectangle.portrait.and.arrow.right")
            }
        } label: {
            Image(systemName: "line.3.horizontal")
        }
        .accessibilityLabel("Mo menu")
    }
}
