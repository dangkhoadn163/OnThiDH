import SwiftUI

private enum ReviewLibraryFilter: Hashable {
    case all
    case subject(String)

    var id: String {
        switch self {
        case .all:
            return "all"
        case .subject(let subjectID):
            return subjectID
        }
    }
}

struct ReviewLibraryView: View {
    @EnvironmentObject private var appState: AppState

    @State private var items: [CompletedLibraryItem] = []
    @State private var isLoading = false
    @State private var loadError: String?
    @State private var searchText = ""
    @State private var selectedFilter: ReviewLibraryFilter = .all

    private var availableSubjects: [SubjectDefinition] {
        var seen = Set<String>()
        return items.compactMap { entry in
            guard seen.insert(entry.subject.id).inserted else {
                return nil
            }
            return entry.subject
        }
        .sorted { $0.title.localizedCaseInsensitiveCompare($1.title) == .orderedAscending }
    }

    private var filteredItems: [CompletedLibraryItem] {
        var result = items

        switch selectedFilter {
        case .all:
            break
        case .subject(let subjectID):
            result = result.filter { $0.subject.id == subjectID }
        }

        if !searchText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            result = result.filter {
                $0.item.title.localizedCaseInsensitiveContains(searchText) ||
                $0.subject.title.localizedCaseInsensitiveContains(searchText) ||
                ($0.item.completedRecord?.nametest?.localizedCaseInsensitiveContains(searchText) ?? false)
            }
        }

        return result
    }

    var body: some View {
        Group {
            if isLoading {
                ProgressView("Dang tai de da lam")
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else if let loadError {
                PlaceholderView(
                    title: "Khong tai duoc de da lam",
                    systemImage: "exclamationmark.triangle",
                    message: loadError
                )
            } else if items.isEmpty {
                PlaceholderView(
                    title: "Chua co de da lam",
                    systemImage: "checklist",
                    message: "Sau khi nop bai, de se xuat hien tai day de xem lai nhanh."
                )
            } else {
                VStack(spacing: 12) {
                    reviewSummary
                    filterScroller

                    List(filteredItems) { entry in
                        NavigationLink {
                            ExamDetailView(subject: entry.subject, item: entry.item)
                        } label: {
                            VStack(alignment: .leading, spacing: 8) {
                                HStack {
                                    Text(entry.item.title)
                                        .font(.headline)
                                    Spacer()
                                    Text(entry.subject.title)
                                        .font(.caption.weight(.semibold))
                                        .padding(.horizontal, 10)
                                        .padding(.vertical, 6)
                                        .background(Color.blue.opacity(0.12), in: Capsule())
                                        .foregroundStyle(.blue)
                                }

                                HStack {
                                    if let score = entry.item.completedRecord?.dapandalam?.score {
                                        Label("Diem: \(score)", systemImage: "checkmark.seal")
                                    }
                                    if let savedName = entry.item.completedRecord?.nametest, !savedName.isEmpty {
                                        Spacer()
                                        Text(savedName)
                                            .lineLimit(1)
                                    }
                                }
                                .font(.footnote)
                                .foregroundStyle(.secondary)
                            }
                            .padding(.vertical, 4)
                        }
                    }
                    .listStyle(.plain)
                }
                .padding(.horizontal)
                .padding(.top)
            }
        }
        .navigationTitle("De da lam")
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                NavigationMenuButton()
            }
        }
        .searchable(text: $searchText, prompt: "Tim de hoac mon hoc")
        .refreshable {
            await loadLibrary(force: true)
        }
        .task {
            await loadLibrary()
        }
    }

    private var reviewSummary: some View {
        HStack(spacing: 12) {
            summaryBox(title: "Tong de", value: "\(items.count)", tint: .blue)
            summaryBox(title: "Mon da hoc", value: "\(availableSubjects.count)", tint: .green)
            summaryBox(title: "Dang hien", value: "\(filteredItems.count)", tint: .orange)
        }
    }

    private var filterScroller: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 10) {
                filterButton(title: "Tat ca", count: items.count, isSelected: selectedFilter == .all, tint: .blue) {
                    selectedFilter = .all
                }

                ForEach(availableSubjects) { subject in
                    filterButton(
                        title: subject.title,
                        count: items.filter { $0.subject.id == subject.id }.count,
                        isSelected: selectedFilter == .subject(subject.id),
                        tint: .teal
                    ) {
                        selectedFilter = .subject(subject.id)
                    }
                }
            }
        }
    }

    private func filterButton(title: String, count: Int, isSelected: Bool, tint: Color, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            HStack(spacing: 8) {
                Text(title)
                    .font(.subheadline.weight(.semibold))
                Text("\(count)")
                    .font(.caption.weight(.bold))
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(.white.opacity(isSelected ? 0.30 : 0.92), in: Capsule())
            }
            .foregroundStyle(isSelected ? Color.white : tint)
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(isSelected ? tint : tint.opacity(0.12))
            )
        }
        .buttonStyle(.plain)
    }

    private func summaryBox(title: String, value: String, tint: Color) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.caption)
                .foregroundStyle(.secondary)
            Text(value)
                .font(.headline)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(12)
        .background(tint.opacity(0.12), in: RoundedRectangle(cornerRadius: 14))
    }

    private func loadLibrary(force: Bool = false) async {
        guard (force || !isLoading), let session = appState.session else {
            return
        }

        isLoading = true
        loadError = nil

        do {
            items = try await appState.databaseClient.fetchCompletedLibrary(uid: session.localID)
        } catch {
            loadError = error.localizedDescription
        }

        isLoading = false
    }
}
