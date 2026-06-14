import SwiftUI

private enum AttemptSegment: String, CaseIterable, Identifiable {
    case exam = "De thi"
    case answers = "Tra loi"

    var id: String { rawValue }
}

struct ExamAttemptView: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.scenePhase) private var scenePhase
    @EnvironmentObject private var appState: AppState

    let subject: SubjectDefinition
    let item: ExamListItem
    let onSaved: () -> Void

    @State private var selections: [AnswerChoice?]
    @State private var selectedSegment: AttemptSegment = .exam
    @State private var secondsRemaining: Int
    @State private var hasSubmitted = false
    @State private var isSubmitting = false
    @State private var showSubmitConfirmation = false
    @State private var submittedItem: ExamListItem?
    @State private var localErrorMessage: String?
    @State private var hasRestoredDraft = false
    @State private var didRestoreDraft = false
    @State private var lastDraftSaveDate: Date?
    @State private var focusedQuestionNumber: Int?
    @State private var selectedPage: ExamPage?
    @State private var hasStartedExam = false

    private let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()

    init(subject: SubjectDefinition, item: ExamListItem, onSaved: @escaping () -> Void) {
        self.subject = subject
        self.item = item
        self.onSaved = onSaved

        let questionCount = subject.examRule.questionCount

        _selections = State(initialValue: Array(repeating: nil, count: questionCount))
        _secondsRemaining = State(initialValue: subject.examRule.durationSeconds)
    }

    private var answerKey: [AnswerChoice?] {
        ExamScoring.answerKey(from: item.exam.answer, expectedQuestionCount: questionCount)
    }

    private var questionCount: Int {
        subject.examRule.questionCount
    }

    private var answeredCount: Int {
        selections.compactMap { $0 }.count
    }

    private var timeString: String {
        let minutes = secondsRemaining / 60
        let seconds = secondsRemaining % 60
        return String(format: "%02d:%02d", minutes, seconds)
    }

    var body: some View {
        Group {
            if let submittedItem {
                ExamDetailView(subject: subject, item: submittedItem)
            } else {
                ZStack {
                    ScrollView {
                        VStack(alignment: .leading, spacing: 16) {
                            VStack(alignment: .leading, spacing: 8) {
                                Text(item.title)
                                    .font(.title3.weight(.semibold))
                                HStack {
                                    Label(timeString, systemImage: "timer")
                                    Spacer()
                                    Text("Da tra loi \(answeredCount)/\(questionCount)")
                                }
                                .font(.subheadline)
                                .foregroundStyle(.secondary)

                                if didRestoreDraft || lastDraftSaveDate != nil {
                                    draftStatusCard
                                }
                            }

                            Picker("Noi dung", selection: $selectedSegment) {
                                ForEach(AttemptSegment.allCases) { segment in
                                    Text(segment.rawValue).tag(segment)
                                }
                            }
                            .pickerStyle(.segmented)

                            segmentShortcutBar

                            if selectedSegment == .exam {
                                examImagesView
                            } else {
                                answersView
                            }
                        }
                        .padding()
                    }
                    .blur(radius: hasStartedExam ? 0 : 2)
                    .disabled(!hasStartedExam)

                    if !hasStartedExam {
                        StartExamOverlay(
                            title: item.exam.testPages.isEmpty ? "Ban da san sang thi chua?" : "De da tai xong. Ban da san sang thi chua?",
                            subtitle: didRestoreDraft
                            ? "Bai lam do da duoc phuc hoi. Bam Tiep tuc de bat dau lai dong ho."
                            : "Dong ho chi bat dau sau khi ban bam Bat dau, giong flow Android.",
                            startTitle: didRestoreDraft ? "Tiep tuc" : "Bat dau",
                            onStart: {
                                hasStartedExam = true
                            },
                            onExit: {
                                dismiss()
                            }
                        )
                    }
                }
                .navigationTitle("Lam bai")
                .navigationBarTitleDisplayMode(.inline)
                .navigationBarBackButtonHidden(true)
                .toolbar {
                    ToolbarItem(placement: .topBarLeading) {
                        Button {
                            if hasStartedExam && !hasSubmitted {
                                localErrorMessage = "Ban phai nop bai hay doi het thoi gian moi duoc thoat nhe !"
                            } else {
                                dismiss()
                            }
                        } label: {
                            Image(systemName: "chevron.left")
                        }
                    }
                    ToolbarItem(placement: .topBarTrailing) {
                        Button("Nop bai") {
                            showSubmitConfirmation = true
                        }
                        .disabled(isSubmitting || hasSubmitted || !hasStartedExam)
                    }
                    ToolbarItem(placement: .topBarTrailing) {
                        NavigationMenuButton()
                    }
                }
                .confirmationDialog("Nop bai thi?", isPresented: $showSubmitConfirmation, titleVisibility: .visible) {
                    Button("Nop bai") {
                        Task {
                            await submit(fillMissingAnswers: false)
                        }
                    }
                    Button("Huy", role: .cancel) {}
                } message: {
                    Text("Ban se nop bai voi \(answeredCount)/\(questionCount) cau da tra loi.")
                }
                .alert("Thong bao", isPresented: Binding(
                    get: { localErrorMessage != nil },
                    set: { isPresented in
                        if !isPresented {
                            localErrorMessage = nil
                        }
                    }
                )) {
                    Button("OK", role: .cancel) {}
                } message: {
                    Text(localErrorMessage ?? "")
                }
                .onReceive(timer) { _ in
                    guard hasStartedExam, !hasSubmitted, secondsRemaining > 0 else {
                        return
                    }

                    secondsRemaining -= 1
                    if secondsRemaining == 0 {
                        Task {
                            await submit(fillMissingAnswers: true)
                        }
                    } else if secondsRemaining % 10 == 0 {
                        persistDraftIfPossible()
                    }
                }
                .task(id: appState.session?.localID) {
                    restoreDraftIfNeeded()
                }
                .onChange(of: selections) { _ in
                    persistDraftIfPossible()
                }
                .onChange(of: scenePhase) { newPhase in
                    if newPhase != .active {
                        persistDraftIfPossible()
                    }
                }
                .onDisappear {
                    persistDraftIfPossible()
                }
                .sheet(item: $selectedPage) { page in
                    ExamPageGallerySheet(pages: item.exam.testPages, initialPageID: page.id)
                }
            }
        }
    }

    private var draftStatusCard: some View {
        HStack(alignment: .top, spacing: 10) {
            Image(systemName: didRestoreDraft ? "arrow.clockwise.circle.fill" : "square.and.arrow.down.fill")
                .foregroundStyle(didRestoreDraft ? Color.green : Color.blue)

            VStack(alignment: .leading, spacing: 4) {
                Text(didRestoreDraft ? "Da phuc hoi bai lam do tren thiet bi." : "Bai lam dang duoc luu tam tren thiet bi.")
                    .font(.footnote.weight(.medium))

                if let lastDraftSaveDate {
                    Text("Cap nhat lan cuoi luc \(lastDraftSaveDate.formatted(date: .omitted, time: .shortened))")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
            }

            Spacer()
        }
        .padding(12)
        .background(
            (didRestoreDraft ? Color.green : Color.blue).opacity(0.10),
            in: RoundedRectangle(cornerRadius: 12)
        )
    }

    private var examImagesView: some View {
        VStack(alignment: .leading, spacing: 12) {
            if item.exam.testPages.isEmpty {
                PlaceholderView(
                    title: "De nay chua co image",
                    systemImage: "photo",
                    message: nil
                )
                .frame(minHeight: 240)
            } else {
                Text("Cham vao anh de mo full-screen. Trong viewer ban co the pinch hoac double tap de zoom.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)

                ForEach(item.exam.testPages) { page in
                    ExamPageCardView(page: page) {
                        selectedPage = page
                    }
                }
            }
        }
    }

    private var answersView: some View {
        ScrollViewReader { proxy in
            VStack(alignment: .leading, spacing: 12) {
                if secondsRemaining <= 300 {
                    Text("Con 5 phut cuoi. Neu het gio, bai se tu dong nop.")
                        .font(.footnote)
                        .foregroundStyle(.orange)
                }

                answerMapView(proxy: proxy)

                ForEach(0..<questionCount, id: \.self) { index in
                    AnswerRow(
                        questionNumber: index + 1,
                        selection: Binding(
                            get: { selections[index] },
                            set: { selections[index] = $0 }
                        ),
                        isFocused: focusedQuestionNumber == (index + 1)
                    )
                    .id(index + 1)
                }

                Button {
                    showSubmitConfirmation = true
                } label: {
                    if isSubmitting {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                    } else {
                        Text("Nop bai")
                            .frame(maxWidth: .infinity)
                    }
                }
                .buttonStyle(.borderedProminent)
                .disabled(isSubmitting || hasSubmitted)
                .padding(.top, 8)
            }
        }
    }

    private var segmentShortcutBar: some View {
        HStack(spacing: 10) {
            if selectedSegment == .exam {
                Button {
                    selectedSegment = .answers
                } label: {
                    Label("Sang tra loi", systemImage: "square.and.pencil")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.borderedProminent)
            } else {
                Button {
                    selectedSegment = .exam
                } label: {
                    Label("Xem lai de", systemImage: "doc.text.image")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
            }

            Text("\(answeredCount)/\(questionCount) cau")
                .font(.footnote.weight(.medium))
                .padding(.horizontal, 12)
                .padding(.vertical, 10)
                .background(Color(.secondarySystemBackground), in: Capsule())
        }
    }

    @MainActor
    private func submit(fillMissingAnswers: Bool) async {
        guard !hasSubmitted else {
            return
        }

        if !fillMissingAnswers && answeredCount < questionCount {
            localErrorMessage = "Ban chua tra loi het \(questionCount) cau."
            return
        }

        guard appState.session != nil else {
            localErrorMessage = "Khong tim thay phien dang nhap."
            return
        }

        isSubmitting = true
        let submission = ExamScoring.submission(
            selections: selections,
            answerKey: answerKey,
            fillMissingAnswers: fillMissingAnswers,
            scorePerAnswer: subject.examRule.scorePerAnswer
        )

        do {
            try await appState.saveExamResult(
                subjectID: subject.id,
                examID: item.id,
                examTitle: item.exam.text,
                answerString: submission.answerString,
                scoreString: submission.scoreString
            )
            hasSubmitted = true
            if let session = appState.session {
                clearDraft(for: session)
            }
            lastDraftSaveDate = nil
            didRestoreDraft = false
            onSaved()
            submittedItem = ExamListItem(
                id: item.id,
                title: item.exam.text,
                exam: item.exam,
                completedRecord: CompletedExamRecord(
                    dapandalam: CompletedAnswer(answer: submission.answerString, score: submission.scoreString),
                    nametest: item.exam.text
                )
            )
        } catch {
            localErrorMessage = error.localizedDescription
        }

        isSubmitting = false
    }

    private func restoreDraftIfNeeded() {
        guard !hasRestoredDraft else {
            return
        }

        hasRestoredDraft = true

        guard let session = appState.session,
              let draft = ExamDraftStore.load(uid: session.localID, subjectID: subject.id, examID: item.id) else {
            return
        }

        selections = restoredSelections(from: draft.selectionRawValues)
        secondsRemaining = min(max(draft.secondsRemaining, 1), subject.examRule.durationSeconds)
        lastDraftSaveDate = draft.savedAt
        didRestoreDraft = true
    }

    private func persistDraftIfPossible() {
        guard !hasSubmitted, let session = appState.session else {
            return
        }

        let draft = ExamDraft(
            selectionRawValues: selections.map { $0?.rawValue },
            secondsRemaining: max(secondsRemaining, 1),
            savedAt: Date()
        )

        ExamDraftStore.save(draft, uid: session.localID, subjectID: subject.id, examID: item.id)
        lastDraftSaveDate = draft.savedAt
    }

    private func answerMapView(proxy: ScrollViewProxy) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("So do tra loi")
                .font(.headline)

            LazyVGrid(
                columns: Array(repeating: GridItem(.flexible(), spacing: 8), count: 5),
                spacing: 8
            ) {
                ForEach(0..<questionCount, id: \.self) { index in
                    let questionNumber = index + 1
                    let isAnswered = selections[index] != nil

                    Button {
                        focusedQuestionNumber = questionNumber
                        withAnimation(.easeInOut(duration: 0.2)) {
                            proxy.scrollTo(questionNumber, anchor: .top)
                        }
                    } label: {
                        Text("\(questionNumber)")
                            .font(.subheadline.weight(.semibold))
                            .frame(maxWidth: .infinity)
                            .frame(height: 38)
                            .background(answerMapTint(isAnswered: isAnswered).opacity(0.16), in: RoundedRectangle(cornerRadius: 10))
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(
                                        focusedQuestionNumber == questionNumber
                                        ? answerMapTint(isAnswered: isAnswered)
                                        : answerMapTint(isAnswered: isAnswered).opacity(0.35),
                                        lineWidth: focusedQuestionNumber == questionNumber ? 2 : 1
                                    )
                            )
                            .foregroundStyle(answerMapTint(isAnswered: isAnswered))
                    }
                    .buttonStyle(.plain)
                }
            }

            HStack(spacing: 12) {
                answerLegend(title: "Da chon", tint: .green)
                answerLegend(title: "Chua chon", tint: .orange)
            }
            .font(.caption)
        }
        .padding()
        .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 14))
    }

    private func clearDraft(for session: AuthSession) {
        ExamDraftStore.clear(uid: session.localID, subjectID: subject.id, examID: item.id)
    }

    private func restoredSelections(from rawValues: [String?]) -> [AnswerChoice?] {
        var restored = Array<AnswerChoice?>(repeating: nil, count: questionCount)

        for index in 0..<min(restored.count, rawValues.count) {
            guard let rawValue = rawValues[index] else {
                continue
            }

            restored[index] = AnswerChoice(rawValue: rawValue)
        }

        return restored
    }

    private func answerMapTint(isAnswered: Bool) -> Color {
        isAnswered ? .green : .orange
    }

    private func answerLegend(title: String, tint: Color) -> some View {
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

private struct StartExamOverlay: View {
    let title: String
    let subtitle: String
    let startTitle: String
    let onStart: () -> Void
    let onExit: () -> Void

    var body: some View {
        ZStack {
            Color.black.opacity(0.18)
                .ignoresSafeArea()

            VStack(spacing: 0) {
                Text(title)
                    .font(.headline)
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 16)
                    .background(Color.blue)

                VStack(spacing: 16) {
                    Text(subtitle)
                        .font(.subheadline)
                        .multilineTextAlignment(.center)
                        .foregroundStyle(.secondary)

                    HStack(spacing: 12) {
                        Button("Thoat", action: onExit)
                            .buttonStyle(.bordered)
                            .tint(.red)
                            .frame(maxWidth: .infinity)

                        Button(startTitle, action: onStart)
                            .buttonStyle(.borderedProminent)
                            .frame(maxWidth: .infinity)
                    }
                }
                .padding(20)
                .background(Color(.systemBackground))
            }
            .clipShape(RoundedRectangle(cornerRadius: 18))
            .shadow(radius: 20)
            .padding(.horizontal, 28)
        }
    }
}

private struct AnswerRow: View {
    let questionNumber: Int
    @Binding var selection: AnswerChoice?
    let isFocused: Bool

    var body: some View {
        HStack(spacing: 10) {
            Text("Cau \(questionNumber)")
                .font(.body.weight(.medium))
                .frame(width: 68, alignment: .leading)

            ForEach(AnswerChoice.allCases) { choice in
                Button {
                    selection = selection == choice ? nil : choice
                } label: {
                    Text(choice.title)
                        .font(.subheadline.weight(.semibold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 10)
                        .background(
                            RoundedRectangle(cornerRadius: 10)
                                .fill(selection == choice ? Color.accentColor : Color(.secondarySystemBackground))
                        )
                        .foregroundStyle(selection == choice ? Color.white : Color.primary)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(12)
        .background(
            (isFocused ? Color.accentColor.opacity(0.10) : Color.clear),
            in: RoundedRectangle(cornerRadius: 12)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(isFocused ? Color.accentColor.opacity(0.35) : Color.clear, lineWidth: 1.5)
        )
    }
}
