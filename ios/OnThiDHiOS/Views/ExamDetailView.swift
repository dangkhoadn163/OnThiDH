import SwiftUI

private enum ReviewSegment: String, CaseIterable, Identifiable {
    case result = "Ket qua"
    case pages = "De thi"
    case detail = "Chi tiet KQ"

    var id: String { rawValue }
}

private enum ReviewFilter: String, CaseIterable, Identifiable {
    case all = "Tat ca"
    case incorrect = "Sai"
    case unanswered = "Bo trong"
    case correct = "Dung"

    var id: String { rawValue }

    var tint: Color {
        switch self {
        case .all:
            return .blue
        case .incorrect:
            return .red
        case .unanswered:
            return .orange
        case .correct:
            return .green
        }
    }

    func matches(_ review: QuestionReview) -> Bool {
        switch self {
        case .all:
            return true
        case .incorrect:
            return review.status == .incorrect
        case .unanswered:
            return review.status == .unanswered
        case .correct:
            return review.status == .correct
        }
    }
}

struct ExamDetailView: View {
    let subject: SubjectDefinition
    let item: ExamListItem

    @State private var selectedSegment: ReviewSegment = .result
    @State private var selectedFilter: ReviewFilter = .all
    @State private var focusedQuestionNumber: Int?
    @State private var selectedPage: ExamPage?
    @State private var selectedGalleryPages: [ExamPage] = []

    private var questionReviews: [QuestionReview] {
        ExamScoring.review(
            userAnswerString: item.completedRecord?.dapandalam?.answer,
            answerKeyRaw: item.exam.answer,
            expectedQuestionCount: subject.examRule.questionCount
        )
    }

    private var filteredReviews: [QuestionReview] {
        questionReviews.filter { selectedFilter.matches($0) }
    }

    private var correctCount: Int {
        questionReviews.filter { $0.status == .correct }.count
    }

    private var unansweredCount: Int {
        questionReviews.filter { $0.status == .unanswered }.count
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text(item.title)
                    .font(.title3.weight(.semibold))

                Text(subject.title)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)

                Picker("Noi dung", selection: $selectedSegment) {
                    ForEach(ReviewSegment.allCases) { segment in
                        Text(segment.rawValue).tag(segment)
                    }
                }
                .pickerStyle(.segmented)

                switch selectedSegment {
                case .result:
                    resultView
                case .pages:
                    pageView
                case .detail:
                    detailResultPageView
                }
            }
            .padding()
        }
        .navigationTitle("Chi tiet de")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                NavigationMenuButton()
            }
        }
        .sheet(item: $selectedPage) { page in
            ExamPageGallerySheet(pages: selectedGalleryPages, initialPageID: page.id)
        }
    }

    private var resultView: some View {
        ScrollViewReader { proxy in
            VStack(alignment: .leading, spacing: 16) {
                if let result = item.completedRecord?.dapandalam {
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Tong quan")
                            .font(.headline)

                        HStack {
                            summaryChip(
                                title: "Diem",
                                value: result.score ?? "--",
                                tint: .blue
                            )
                            summaryChip(
                                title: "Dung",
                                value: "\(correctCount)/\(subject.examRule.questionCount)",
                                tint: .green
                            )
                            summaryChip(
                                title: "Bo trong",
                                value: "\(unansweredCount)",
                                tint: .orange
                            )
                        }

                        if let answer = result.answer, !answer.isEmpty {
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Chuoi dap an da luu")
                                    .font(.subheadline.weight(.medium))
                                Text(answer)
                                    .font(.footnote.monospaced())
                                    .textSelection(.enabled)
                            }
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 14))
                }

                questionMapView(proxy: proxy)

                VStack(alignment: .leading, spacing: 10) {
                    Text("Chi tiet tung cau")
                        .font(.headline)

                    filterChips

                    if filteredReviews.isEmpty {
                        PlaceholderView(
                            title: "Khong co cau phu hop",
                            systemImage: "line.3.horizontal.decrease.circle",
                            message: "Thu doi bo loc de xem nhom cau khac."
                        )
                    } else {
                        ForEach(filteredReviews) { review in
                            QuestionReviewRow(
                                review: review,
                                isFocused: focusedQuestionNumber == review.questionNumber
                            )
                            .id(review.questionNumber)
                        }
                    }
                }
            }
        }
    }

    private var pageView: some View {
        VStack(alignment: .leading, spacing: 12) {
            if item.exam.testPages.isEmpty {
                PlaceholderView(
                    title: "De nay chua co image",
                    systemImage: "photo",
                    message: nil
                )
            } else {
                Text("Cham vao anh de mo full-screen. Ban co the pinch hoac double tap de zoom.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)

                ForEach(item.exam.testPages) { page in
                    ExamPageCardView(page: page) {
                        selectedGalleryPages = item.exam.testPages
                        selectedPage = page
                    }
                }
            }
        }
    }

    private var detailResultPageView: some View {
        VStack(alignment: .leading, spacing: 12) {
            if item.exam.detailResultPages.isEmpty {
                PlaceholderView(
                    title: "De nay chua co anh chi tiet ket qua",
                    systemImage: "doc.text.image",
                    message: "Firebase chua co nhanh `detailresult` cho de nay."
                )
            } else {
                Text("Anh `detailresult` duoc doc rieng de giong tab Chi tiet ket qua ben Android.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)

                ForEach(item.exam.detailResultPages) { page in
                    ExamPageCardView(page: page) {
                        selectedGalleryPages = item.exam.detailResultPages
                        selectedPage = page
                    }
                }
            }
        }
    }

    private func summaryChip(title: String, value: String, tint: Color) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.caption)
                .foregroundStyle(.secondary)
            Text(value)
                .font(.headline)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(10)
        .background(tint.opacity(0.12), in: RoundedRectangle(cornerRadius: 12))
    }

    private func questionMapView(proxy: ScrollViewProxy) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("So do cau hoi")
                .font(.headline)

            LazyVGrid(
                columns: Array(repeating: GridItem(.flexible(), spacing: 8), count: 5),
                spacing: 8
            ) {
                ForEach(questionReviews) { review in
                    Button {
                        selectedFilter = .all
                        focusedQuestionNumber = review.questionNumber
                        DispatchQueue.main.async {
                            withAnimation(.easeInOut(duration: 0.2)) {
                                proxy.scrollTo(review.questionNumber, anchor: .top)
                            }
                        }
                    } label: {
                        Text("\(review.questionNumber)")
                            .font(.subheadline.weight(.semibold))
                            .frame(maxWidth: .infinity)
                            .frame(height: 38)
                            .background(questionMapColor(for: review).opacity(0.16), in: RoundedRectangle(cornerRadius: 10))
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(
                                        focusedQuestionNumber == review.questionNumber
                                        ? questionMapColor(for: review)
                                        : questionMapColor(for: review).opacity(0.35),
                                        lineWidth: focusedQuestionNumber == review.questionNumber ? 2 : 1
                                    )
                            )
                            .foregroundStyle(questionMapColor(for: review))
                    }
                    .buttonStyle(.plain)
                }
            }

            HStack(spacing: 12) {
                questionLegend(title: "Dung", tint: .green)
                questionLegend(title: "Sai", tint: .red)
                questionLegend(title: "Bo trong", tint: .orange)
            }
            .font(.caption)
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 14))
    }

    private var filterChips: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 10) {
                ForEach(ReviewFilter.allCases) { filter in
                    Button {
                        selectedFilter = filter
                        if filter != .all && focusedQuestionNumber != nil {
                            focusedQuestionNumber = nil
                        }
                    } label: {
                        HStack(spacing: 8) {
                            Text(filter.rawValue)
                                .font(.subheadline.weight(.semibold))
                            Text("\(count(for: filter))")
                                .font(.caption.weight(.bold))
                                .padding(.horizontal, 8)
                                .padding(.vertical, 4)
                                .background(.white.opacity(selectedFilter == filter ? 0.28 : 0.92), in: Capsule())
                        }
                        .foregroundStyle(selectedFilter == filter ? Color.white : filter.tint)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 10)
                        .background(
                            RoundedRectangle(cornerRadius: 12)
                                .fill(selectedFilter == filter ? filter.tint : filter.tint.opacity(0.12))
                        )
                    }
                    .buttonStyle(.plain)
                }
            }
        }
    }

    private func count(for filter: ReviewFilter) -> Int {
        questionReviews.filter { filter.matches($0) }.count
    }

    private func questionMapColor(for review: QuestionReview) -> Color {
        switch review.status {
        case .correct:
            return .green
        case .incorrect:
            return .red
        case .unanswered:
            return .orange
        }
    }

    private func questionLegend(title: String, tint: Color) -> some View {
        HStack(spacing: 6) {
            Circle()
                .fill(tint.opacity(0.16))
                .frame(width: 12, height: 12)
                .overlay(
                    Circle()
                        .stroke(tint.opacity(0.5), lineWidth: 1)
                )
            Text(title)
                .foregroundStyle(.secondary)
        }
    }
}

private struct QuestionReviewRow: View {
    let review: QuestionReview
    let isFocused: Bool

    private var statusText: String {
        switch review.status {
        case .correct:
            return "Dung"
        case .incorrect:
            return "Sai"
        case .unanswered:
            return "Bo trong"
        }
    }

    private var statusColor: Color {
        switch review.status {
        case .correct:
            return .green
        case .incorrect:
            return .red
        case .unanswered:
            return .orange
        }
    }

    private var statusIcon: String {
        switch review.status {
        case .correct:
            return "checkmark.circle.fill"
        case .incorrect:
            return "xmark.circle.fill"
        case .unanswered:
            return "exclamationmark.circle.fill"
        }
    }

    private var selectedChoiceTint: Color {
        switch review.status {
        case .correct:
            return .green
        case .incorrect:
            return .red
        case .unanswered:
            return .orange
        }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                Label {
                    Text("Cau \(review.questionNumber)")
                        .font(.body.weight(.semibold))
                } icon: {
                    Image(systemName: statusIcon)
                        .foregroundStyle(statusColor)
                }

                Spacer()
                Text(statusText)
                    .font(.caption.weight(.semibold))
                    .padding(.horizontal, 10)
                    .padding(.vertical, 6)
                    .background(statusColor.opacity(0.12), in: Capsule())
                    .foregroundStyle(statusColor)
            }

            HStack(spacing: 10) {
                reviewChoiceChip(
                    title: "Ban chon",
                    choice: review.selectedChoice?.title ?? "-",
                    tint: selectedChoiceTint
                )
                reviewChoiceChip(
                    title: "Dap an dung",
                    choice: review.correctChoice?.title ?? "-",
                    tint: .green
                )
            }
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(statusColor.opacity(0.10), in: RoundedRectangle(cornerRadius: 14))
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .stroke(isFocused ? statusColor : statusColor.opacity(0.30), lineWidth: isFocused ? 2 : 1)
        )
        .shadow(color: isFocused ? statusColor.opacity(0.20) : .clear, radius: 12, y: 4)
    }

    private func reviewChoiceChip(title: String, choice: String, tint: Color) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.caption)
                .foregroundStyle(.secondary)
            Text(choice)
                .font(.headline)
                .foregroundStyle(tint)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(10)
        .background(tint.opacity(0.08), in: RoundedRectangle(cornerRadius: 12))
    }
}

struct ExamPageCardView: View {
    let page: ExamPage
    let onOpen: () -> Void

    var body: some View {
        Button(action: onOpen) {
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Text(page.title)
                        .font(.headline)
                    Spacer()
                    Label("Mo lon", systemImage: "arrow.up.left.and.arrow.down.right")
                        .font(.caption.weight(.semibold))
                        .padding(.horizontal, 10)
                        .padding(.vertical, 6)
                        .background(Color.blue.opacity(0.12), in: Capsule())
                        .foregroundStyle(.blue)
                }

                AsyncImage(url: page.url) { phase in
                    switch phase {
                    case .empty:
                        ZStack {
                            RoundedRectangle(cornerRadius: 12)
                                .fill(Color(.secondarySystemBackground))
                            ProgressView()
                        }
                        .frame(height: 240)
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFit()
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                    case .failure:
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color(.secondarySystemBackground))
                            .frame(height: 180)
                            .overlay {
                                Text("Khong tai duoc anh")
                                    .foregroundStyle(.secondary)
                            }
                    @unknown default:
                        EmptyView()
                    }
                }
            }
        }
        .buttonStyle(.plain)
    }
}

struct ExamPageGallerySheet: View {
    @Environment(\.dismiss) private var dismiss

    let pages: [ExamPage]
    let initialPageID: String

    @State private var selectedPageID: String

    init(pages: [ExamPage], initialPageID: String) {
        self.pages = pages
        self.initialPageID = initialPageID
        _selectedPageID = State(initialValue: initialPageID)
    }

    private var currentIndex: Int {
        pages.firstIndex(where: { $0.id == selectedPageID }).map { $0 + 1 } ?? 1
    }

    private var currentPage: ExamPage? {
        pages.first(where: { $0.id == selectedPageID })
    }

    var body: some View {
        NavigationStack {
            ZStack(alignment: .bottom) {
                Color.black.ignoresSafeArea()

                TabView(selection: $selectedPageID) {
                    ForEach(pages) { page in
                        ZoomableRemoteImageView(url: page.url)
                            .tag(page.id)
                    }
                }
                .tabViewStyle(.page(indexDisplayMode: .never))

                VStack(spacing: 10) {
                    Text("Pinch hoac double tap de zoom")
                        .font(.caption.weight(.medium))
                        .foregroundStyle(.white.opacity(0.78))

                    Text("\(currentIndex)/\(pages.count)")
                        .font(.caption.weight(.bold))
                        .foregroundStyle(.white)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(Color.white.opacity(0.16), in: Capsule())
                }
                .padding(.bottom, 18)
            }
            .navigationTitle(currentPage?.title ?? "De thi")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Dong") {
                        dismiss()
                    }
                    .foregroundStyle(.white)
                }
            }
            .toolbarBackground(.hidden, for: .navigationBar)
        }
        .preferredColorScheme(.dark)
    }
}

struct ZoomableRemoteImageView: View {
    let url: URL

    @State private var scale: CGFloat = 1
    @State private var baseScale: CGFloat = 1
    @State private var offset: CGSize = .zero
    @State private var baseOffset: CGSize = .zero

    var body: some View {
        GeometryReader { proxy in
            ZStack {
                Color.black

                AsyncImage(url: url) { phase in
                    switch phase {
                    case .empty:
                        ProgressView()
                            .tint(.white)
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFit()
                            .scaleEffect(scale)
                            .offset(offset)
                            .gesture(magnificationGesture(in: proxy.size))
                            .simultaneousGesture(dragGesture(in: proxy.size))
                            .onTapGesture(count: 2) {
                                withAnimation(.easeInOut(duration: 0.2)) {
                                    if scale > 1.1 {
                                        resetZoom()
                                    } else {
                                        scale = 2
                                        baseScale = 2
                                    }
                                }
                            }
                    case .failure:
                        VStack(spacing: 12) {
                            Image(systemName: "wifi.slash")
                                .font(.title2)
                            Text("Khong tai duoc anh")
                                .font(.subheadline)
                        }
                        .foregroundStyle(.white.opacity(0.8))
                    @unknown default:
                        EmptyView()
                    }
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }

    private func magnificationGesture(in size: CGSize) -> some Gesture {
        MagnificationGesture()
            .onChanged { value in
                scale = min(max(baseScale * value, 1), 4)
                offset = clamped(offset: offset, in: size)
            }
            .onEnded { _ in
                baseScale = scale
                offset = clamped(offset: offset, in: size)
                baseOffset = offset
                if scale <= 1.01 {
                    resetZoom()
                }
            }
    }

    private func dragGesture(in size: CGSize) -> some Gesture {
        DragGesture()
            .onChanged { value in
                guard scale > 1 else {
                    return
                }

                let proposed = CGSize(
                    width: baseOffset.width + value.translation.width,
                    height: baseOffset.height + value.translation.height
                )
                offset = clamped(offset: proposed, in: size)
            }
            .onEnded { _ in
                guard scale > 1 else {
                    resetZoom()
                    return
                }
                baseOffset = offset
            }
    }

    private func clamped(offset: CGSize, in size: CGSize) -> CGSize {
        let horizontalLimit = max(((size.width * scale) - size.width) / 2, 0)
        let verticalLimit = max(((size.height * scale) - size.height) / 2, 0)
        return CGSize(
            width: min(max(offset.width, -horizontalLimit), horizontalLimit),
            height: min(max(offset.height, -verticalLimit), verticalLimit)
        )
    }

    private func resetZoom() {
        scale = 1
        baseScale = 1
        offset = .zero
        baseOffset = .zero
    }
}
