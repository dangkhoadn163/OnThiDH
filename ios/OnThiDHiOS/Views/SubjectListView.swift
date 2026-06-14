import SwiftUI

struct SubjectListView: View {
    @EnvironmentObject private var appState: AppState

    @State private var subjects: [SubjectDefinition] = []
    @State private var isLoading = false
    @State private var loadError: String?

    var body: some View {
        Group {
            if isLoading {
                ProgressView("Dang tai danh sach mon")
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if let loadError {
                PlaceholderView(
                    title: "Khong tai duoc du lieu",
                    systemImage: "wifi.slash",
                    message: loadError
                )
            } else {
                List {
                    if let session = appState.session {
                        Section {
                            SubjectHeaderCard(
                                name: appState.profile?.name ?? "Hoc sinh",
                                email: appState.profile?.email ?? session.email,
                                avatarURL: appState.profile?.avatar,
                                subjectCount: subjects.count
                            )
                            .listRowInsets(EdgeInsets(top: 8, leading: 16, bottom: 8, trailing: 16))
                            .listRowBackground(Color.clear)
                        }
                    }

                    Section("Chon mon hoc") {
                        ForEach(subjects) { subject in
                            NavigationLink {
                                SubjectHomeView(subject: subject)
                            } label: {
                                HStack(spacing: 12) {
                                    ZStack {
                                        RoundedRectangle(cornerRadius: 12)
                                            .fill(subjectTint(for: subject).opacity(0.14))
                                            .frame(width: 42, height: 42)
                                        Image(systemName: subjectIcon(for: subject))
                                            .foregroundStyle(subjectTint(for: subject))
                                    }

                                    VStack(alignment: .leading, spacing: 4) {
                                        Text(subject.title)
                                            .font(.headline)
                                        Text("De thi, bai tap va tai lieu")
                                            .font(.footnote)
                                            .foregroundStyle(.secondary)
                                    }
                                }
                                .padding(.vertical, 4)
                            }
                        }
                    }
                }
                .listStyle(.insetGrouped)
            }
        }
        .navigationTitle("Mon hoc")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                NavigationMenuButton()
            }
        }
        .refreshable {
            await appState.refreshProfile()
            await loadSubjects(force: true)
        }
        .task {
            await loadSubjects()
        }
    }

    private func loadSubjects(force: Bool = false) async {
        guard force || !isLoading else {
            return
        }

        isLoading = true
        loadError = nil

        do {
            let databaseClient = appState.databaseClient
            subjects = try await databaseClient.fetchSubjects()
        } catch {
            loadError = error.localizedDescription
        }

        isLoading = false
    }

    private func subjectIcon(for subject: SubjectDefinition) -> String {
        switch subject.id {
        case "toanhoc":
            return "function"
        case "anhvan":
            return "text.book.closed"
        case "vatly":
            return "atom"
        case "hoahoc":
            return "flask"
        case "sinhhoc":
            return "leaf"
        case "lichsu":
            return "clock.arrow.circlepath"
        case "dialy":
            return "globe.asia.australia"
        case "gdcd":
            return "person.2"
        default:
            return "doc.text"
        }
    }

    private func subjectTint(for subject: SubjectDefinition) -> Color {
        switch subject.id {
        case "toanhoc":
            return .blue
        case "anhvan":
            return .indigo
        case "vatly":
            return .orange
        case "hoahoc":
            return .pink
        case "sinhhoc":
            return .green
        case "lichsu":
            return .brown
        case "dialy":
            return .teal
        case "gdcd":
            return .mint
        default:
            return .gray
        }
    }
}

private struct SubjectHomeView: View {
    @EnvironmentObject private var appState: AppState

    let subject: SubjectDefinition

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {
                VStack(alignment: .leading, spacing: 10) {
                    Text(subjectHeaderTitle)
                        .font(.title3.weight(.semibold))
                    Text("Ban Android goc di qua man nay truoc khi vao danh sach de. Tren iOS, toi giu lai flow nay de hanh vi gan hon.")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(
                    LinearGradient(
                        colors: [subjectTint.opacity(0.18), subjectTint.opacity(0.06)],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    ),
                    in: RoundedRectangle(cornerRadius: 18)
                )

                NavigationLink {
                    ExamListView(subject: subject)
                } label: {
                    subjectActionCard(
                        title: "De thi",
                        subtitle: "Mo danh sach de chua lam va da lam",
                        systemImage: "doc.text.magnifyingglass",
                        tint: subjectTint
                    )
                }
                .buttonStyle(.plain)

                Button {
                    appState.infoMessage = "Bai tap dang de Coming soon giong Android."
                } label: {
                    subjectActionCard(
                        title: "Bai tap",
                        subtitle: "Coming soon",
                        systemImage: "pencil.and.list.clipboard",
                        tint: .orange
                    )
                }
                .buttonStyle(.plain)

                Button {
                    appState.infoMessage = "Tai lieu dang de Coming soon giong Android."
                } label: {
                    subjectActionCard(
                        title: "Tai lieu",
                        subtitle: "Coming soon",
                        systemImage: "books.vertical",
                        tint: .teal
                    )
                }
                .buttonStyle(.plain)
            }
            .padding()
        }
        .navigationTitle(subject.title)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                NavigationMenuButton()
            }
        }
    }

    private var subjectHeaderTitle: String {
        "Luyen thi mon \(subject.title)"
    }

    private var subjectTint: Color {
        switch subject.id {
        case "toanhoc":
            return .blue
        case "anhvan":
            return .indigo
        case "vatly":
            return .orange
        case "hoahoc":
            return .pink
        case "sinhhoc":
            return .green
        case "lichsu":
            return .brown
        case "dialy":
            return .teal
        case "gdcd":
            return .mint
        default:
            return .gray
        }
    }

    private func subjectActionCard(title: String, subtitle: String, systemImage: String, tint: Color) -> some View {
        HStack(spacing: 14) {
            RoundedRectangle(cornerRadius: 14)
                .fill(tint.opacity(0.14))
                .frame(width: 52, height: 52)
                .overlay {
                    Image(systemName: systemImage)
                        .font(.title3)
                        .foregroundStyle(tint)
                }

            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.headline)
                    .foregroundStyle(.primary)
                Text(subtitle)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }

            Spacer()
            Image(systemName: "chevron.right")
                .foregroundStyle(.tertiary)
        }
        .padding()
        .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 18))
    }
}

private struct SubjectHeaderCard: View {
    let name: String
    let email: String
    let avatarURL: String?
    let subjectCount: Int

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 12) {
                avatarView

                VStack(alignment: .leading, spacing: 4) {
                    Text(name)
                        .font(.headline)
                    Text(email)
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                        .lineLimit(1)
                }
            }

            HStack {
                Label("\(subjectCount) mon dang mo", systemImage: "books.vertical")
                    .font(.footnote.weight(.medium))
                    .foregroundStyle(.secondary)
                Spacer()
                Text("Firebase online")
                    .font(.caption.weight(.semibold))
                    .padding(.horizontal, 10)
                    .padding(.vertical, 6)
                    .background(Color.green.opacity(0.12), in: Capsule())
                    .foregroundStyle(.green)
            }
        }
        .padding()
        .background(
            LinearGradient(
                colors: [Color(.secondarySystemBackground), Color.blue.opacity(0.08)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            ),
            in: RoundedRectangle(cornerRadius: 18)
        )
    }

    private var avatarView: some View {
        ZStack {
            Circle()
                .fill(
                    LinearGradient(
                        colors: [Color.blue, Color.cyan],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .frame(width: 52, height: 52)

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
                            .foregroundStyle(.white)
                    @unknown default:
                        Image(systemName: "person.fill")
                            .foregroundStyle(.white)
                    }
                }
                .frame(width: 52, height: 52)
                .clipShape(Circle())
            } else {
                Image(systemName: "person.fill")
                    .foregroundStyle(.white)
            }
        }
    }
}
