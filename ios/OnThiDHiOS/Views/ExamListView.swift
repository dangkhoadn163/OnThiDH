import SwiftUI

private enum ExamSegment: String, CaseIterable, Identifiable {
    case pending = "Chua lam"
    case completed = "Da lam"

    var id: String { rawValue }
}

struct ExamListView: View {
    @EnvironmentObject private var appState: AppState

    let subject: SubjectDefinition

    @State private var data: ExamSectionData?
    @State private var isLoading = false
    @State private var loadError: String?
    @State private var selectedSegment = ExamSegment.pending
    @State private var reloadToken = UUID()
    @State private var searchText = ""
    @State private var draftExamIDs = Set<String>()

    private var currentItems: [ExamListItem] {
        guard let data else {
            return []
        }

        let items: [ExamListItem]
        switch selectedSegment {
        case .pending:
            items = data.pending.sorted { lhs, rhs in
                let lhsDraft = draftExamIDs.contains(lhs.id)
                let rhsDraft = draftExamIDs.contains(rhs.id)
                if lhsDraft != rhsDraft {
                    return lhsDraft && !rhsDraft
                }
                return lhs.title.localizedCaseInsensitiveCompare(rhs.title) == .orderedAscending
            }
        case .completed:
            items = data.completed
        }

        if searchText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return items
        }

        return items.filter {
            $0.title.localizedCaseInsensitiveContains(searchText) ||
            ($0.completedRecord?.nametest?.localizedCaseInsensitiveContains(searchText) ?? false)
        }
    }

    var body: some View {
        Group {
            if isLoading {
                ProgressView("Dang tai de")
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if let loadError {
                PlaceholderView(
                    title: "Khong tai duoc danh sach de",
                    systemImage: "exclamationmark.triangle",
                    message: loadError
                )
            } else if let data {
                VStack(spacing: 12) {
                    Picker("Trang thai", selection: $selectedSegment) {
                        ForEach(ExamSegment.allCases) { segment in
                            Text(segment.rawValue).tag(segment)
                        }
                    }
                    .pickerStyle(.segmented)

                    HStack {
                        Text("Tong de: \(data.availableCount)")
                        Spacer()
                        Text("Da lam: \(data.completed.count)")
                        Spacer()
                        Text("Chua lam: \(data.pending.count)")
                    }
                    .font(.footnote)
                    .foregroundStyle(.secondary)

                    if currentItems.isEmpty {
                        PlaceholderView(
                            title: "Khong co de",
                            systemImage: "doc.text.magnifyingglass",
                            message: nil
                        )
                    } else {
                        List(currentItems) { item in
                            NavigationLink {
                                if selectedSegment == .pending {
                                    ExamAttemptView(subject: subject, item: item) {
                                        reloadToken = UUID()
                                        selectedSegment = .completed
                                    }
                                } else {
                                    ExamDetailView(subject: subject, item: item)
                                }
                            } label: {
                                VStack(alignment: .leading, spacing: 8) {
                                    HStack(alignment: .top) {
                                        VStack(alignment: .leading, spacing: 6) {
                                            Text(item.title)
                                                .font(.headline)

                                            if let score = item.completedRecord?.dapandalam?.score, !score.isEmpty {
                                                Text("Diem: \(score)")
                                                    .font(.subheadline)
                                                    .foregroundStyle(.secondary)
                                            } else {
                                                Text("Chua co ket qua")
                                                    .font(.subheadline)
                                                    .foregroundStyle(.secondary)
                                            }
                                        }

                                        Spacer(minLength: 8)

                                        if selectedSegment == .pending, draftExamIDs.contains(item.id) {
                                            Text("Dang lam do")
                                                .font(.caption.weight(.semibold))
                                                .padding(.horizontal, 10)
                                                .padding(.vertical, 6)
                                                .background(Color.orange.opacity(0.14), in: Capsule())
                                                .foregroundStyle(.orange)
                                        }
                                    }

                                    if selectedSegment == .pending, draftExamIDs.contains(item.id) {
                                        Text("Mo lai se phuc hoi dap an va thoi gian dang do.")
                                            .font(.caption)
                                            .foregroundStyle(.secondary)
                                    }
                                }
                                .padding(.vertical, 4)
                            }
                        }
                        .listStyle(.plain)
                    }
                }
                .padding(.horizontal)
                .padding(.top)
            }
        }
        .navigationTitle(subject.title)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                NavigationMenuButton()
            }
        }
        .searchable(text: $searchText, prompt: "Tim ten de")
        .refreshable {
            await loadExams(force: true)
        }
        .task(id: reloadToken) {
            await loadExams()
        }
    }

    private func loadExams(force: Bool = false) async {
        guard (force || !isLoading), let session = appState.session else {
            return
        }

        isLoading = true
        loadError = nil

        do {
            let databaseClient = appState.databaseClient
            data = try await databaseClient.fetchExamSections(uid: session.localID, subjectID: subject.id)
            refreshDraftState(uid: session.localID)
        } catch {
            loadError = error.localizedDescription
        }

        isLoading = false
    }

    private func refreshDraftState(uid: String) {
        guard let data else {
            draftExamIDs = []
            return
        }

        draftExamIDs = Set(
            data.pending.compactMap { item in
                ExamDraftStore.hasDraft(uid: uid, subjectID: subject.id, examID: item.id) ? item.id : nil
            }
        )
    }
}
