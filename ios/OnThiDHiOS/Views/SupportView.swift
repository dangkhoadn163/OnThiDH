import SwiftUI
import UIKit

struct SupportView: View {
    @EnvironmentObject private var appState: AppState

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                supportHero

                SupportCard(
                    title: "Dang nhap va dong bo",
                    systemImage: "person.badge.key",
                    tint: .blue,
                    bodyText: "Neu login thanh cong nhung khong thay du lieu, hay thu keo de refresh trong man Mon hoc hoac Da lam. Realtime Database cua app nay dang la nguon du lieu chinh."
                )

                SupportCard(
                    title: "Bai lam dang do",
                    systemImage: "square.and.arrow.down",
                    tint: .orange,
                    bodyText: "De dang lam se tu luu tren thiet bi. Trong danh sach de, nhung bai co nhan Dang lam do se phuc hoi dap an va thoi gian khi mo lai."
                )

                SupportCard(
                    title: "Doi mat khau",
                    systemImage: "lock.rotation",
                    tint: .green,
                    bodyText: "Neu quen mat khau, ban co the gui email reset. Neu con nho mat khau hien tai, vao Tai khoan de doi mat khau truc tiep."
                )

                VStack(alignment: .leading, spacing: 12) {
                    Text("Cong cu ho tro")
                        .font(.headline)

                    actionButton(title: "Copy UID", systemImage: "doc.on.doc") {
                        UIPasteboard.general.string = appState.session?.localID ?? ""
                        appState.infoMessage = "Da copy UID."
                    }

                    actionButton(title: "Copy email", systemImage: "envelope.on.envelope") {
                        UIPasteboard.general.string = appState.session?.email ?? ""
                        appState.infoMessage = "Da copy email."
                    }

                    actionButton(title: "Tai lai profile", systemImage: "arrow.clockwise") {
                        Task {
                            await appState.refreshProfile()
                        }
                    }
                }
            }
            .padding()
        }
        .navigationTitle("Support")
        .navigationBarTitleDisplayMode(.inline)
    }

    private var supportHero: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("Can giup gi?")
                .font(.title3.weight(.semibold))
            Text("Man nay gom cac cach xu ly loi pho bien tren iOS ban port tu Android: login, du lieu Firebase, draft bai lam, va quan ly tai khoan.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(
            LinearGradient(
                colors: [Color.blue.opacity(0.14), Color.cyan.opacity(0.08)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            ),
            in: RoundedRectangle(cornerRadius: 18)
        )
    }

    private func actionButton(title: String, systemImage: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Label(title, systemImage: systemImage)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
        .buttonStyle(.bordered)
    }
}

private struct SupportCard: View {
    let title: String
    let systemImage: String
    let tint: Color
    let bodyText: String

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Label(title, systemImage: systemImage)
                .font(.headline)
                .foregroundStyle(tint)
            Text(bodyText)
                .font(.subheadline)
                .foregroundStyle(.secondary)
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(tint.opacity(0.10), in: RoundedRectangle(cornerRadius: 16))
    }
}
