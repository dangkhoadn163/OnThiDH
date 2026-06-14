import SwiftUI

struct RateView: View {
    @AppStorage("OnThiDHiOS.Rating") private var storedRating = 0
    @AppStorage("OnThiDHiOS.RatingNote") private var storedNote = ""

    @State private var currentRating = 0
    @State private var note = ""
    @State private var didLoad = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {
                VStack(alignment: .leading, spacing: 10) {
                    Text("Danh gia ban port iOS")
                        .font(.title3.weight(.semibold))
                    Text("Android goc chi co placeholder o muc nay. Tren iOS, ban co the luu cam nhan nhanh ngay trong app de theo doi chat luong ban port.")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(
                    LinearGradient(
                        colors: [Color.yellow.opacity(0.18), Color.orange.opacity(0.10)],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    ),
                    in: RoundedRectangle(cornerRadius: 18)
                )

                VStack(alignment: .leading, spacing: 12) {
                    Text("Muc do hai long")
                        .font(.headline)

                    HStack(spacing: 10) {
                        ForEach(1...5, id: \.self) { value in
                            Button {
                                currentRating = value
                                storedRating = value
                            } label: {
                                Image(systemName: value <= currentRating ? "star.fill" : "star")
                                    .font(.title2)
                                    .foregroundStyle(value <= currentRating ? .yellow : .gray.opacity(0.7))
                                    .frame(width: 40, height: 40)
                                    .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 12))
                            }
                            .buttonStyle(.plain)
                        }
                    }

                    if currentRating > 0 {
                        Text(ratingMessage(for: currentRating))
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("Ghi chu")
                        .font(.headline)

                    TextField("Ban thay phan nao can cai thien tiep?", text: $note, axis: .vertical)
                        .textFieldStyle(.roundedBorder)
                        .lineLimit(4...8)

                    Button("Luu danh gia") {
                        storedRating = currentRating
                        storedNote = note.trimmingCharacters(in: .whitespacesAndNewlines)
                    }
                    .buttonStyle(.borderedProminent)
                }

                VStack(alignment: .leading, spacing: 8) {
                    Text("Da luu")
                        .font(.headline)
                    Text("So sao: \(storedRating == 0 ? "Chua co" : "\(storedRating)/5")")
                    Text("Ghi chu: \(storedNote.isEmpty ? "Chua co" : storedNote)")
                        .foregroundStyle(.secondary)
                }
                .font(.subheadline)
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(.secondarySystemBackground), in: RoundedRectangle(cornerRadius: 16))
            }
            .padding()
        }
        .navigationTitle("Rate")
        .navigationBarTitleDisplayMode(.inline)
        .task {
            guard !didLoad else {
                return
            }
            didLoad = true
            currentRating = storedRating
            note = storedNote
        }
    }

    private func ratingMessage(for rating: Int) -> String {
        switch rating {
        case 5:
            return "Rat tot. Ban port iOS da o muc rat gan Android."
        case 4:
            return "Tot. Chi con mot vai chi tiet de polish them."
        case 3:
            return "On. Da dung duoc, nhung van con cho can cai thien."
        case 2:
            return "Can bo sung them feature de sat Android hon."
        default:
            return "Can xem lai UX va do on dinh."
        }
    }
}
