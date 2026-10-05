# Trung Thu NSOKISS / bản gốc 2026

Nguồn: https://dd.ninjaschool.vn/app/index.php?for=forum&do=list&uid=1000147012&p=0&sz=15
Đối chiếu ngày 05/10/2026. Sự kiện kết thúc 23:59:59 ngày 15/10/2026 theo giờ Việt Nam.

## Chọn bản và thay đổi

Bản mới gần luồng thả đèn/rước đèn của server gốc hơn; công thức bánh cũ đúng hơn.
TrungThuNew nay kế thừa công thức, cây trang trí từ TrungThu, thay menu và thưởng.
- Quái: cấp nhân vật >=20, lệch <=7; dùng nhãn phù lệch <=10. Giữ tỉ lệ Kiss 10%, TNP +5%, KNP +3%, Làng Cổ/VDMQ +2% vì bài không công bố con số.
- Mỗi bánh 15.000 yên; sửa lỗi kiểm tra nhưng không trừ yên trong phạm vi Trung Thu mới.
- Bốn loại bánh khóa, dùng tối đa 1.500/loại/ngày. Ngày tính theo Việt Nam, bộ đếm lưu bằng event_point.
- Hộp thường/cao cấp không khóa. Mốc tính số hộp đã dùng, không tính khi chế tạo.
- Tách thưởng hộp thường, cao cấp, thả đèn, hộ tống, boss; không còn dùng bảng trống.
- Tabemono bán giấy thường, đèn; Gosho bán giấy cao cấp, giấy thông hành.
- Giá giấy/đèn giữ Kiss: 30.000 xu / 25 lượng / 120.000 xu / 20 lượng. Bài gốc không ghi giá.
- Gosho bán Nhật tử lam phong/Thiên nguyệt chi nữ 100 lượng, 15 ngày, khóa.
- Đổi Bạch hổ: 30 bánh phong lôi; gậy 7 ngày: 10 bánh phong lôi; gậy 30 ngày: 30 bánh băng hỏa. Thú 5 sao dùng sys=4 và bộ chỉ số Kiss.
- Nâng lồng đèn: 500.000 xu hoặc 5 lượng; bảo toàn option, vĩnh viễn giữ vĩnh viễn, đồ hạn cộng 30 ngày. Đổi lượng thêm Không nhận EXP, có xác suất thêm giảm sát thương 100%. Chỉ đồ tạo từ 22/09/2026.
- Boss Hỏa kỳ lân/Tử hạ ma thần: 12h,19h,21h,23h VN, các map công bố có trong src. Mỗi lượt chọn hai map và khu ngẫu nhiên.
- Rước đèn: cấp >=20, Giấy thông hành; NPC đèn tại Hirosaki, dẫn về trường như luồng Kiss sẵn có. Thưởng riêng.
- TOP chỉ thả đèn, >=5.000 mới xếp hạng; bằng điểm ưu tiên thứ tự đạt điểm. Có luồng nhận thưởng một lần sau khi kết thúc.
- Mốc 3.000 hộp thường: vũ khí 10x đúng hệ, max option, 30 ngày; nếu DB có Quả chakra vàng thì thêm 3 quả. Không có dữ liệu thì bỏ qua.
- Gỡ TOP bánh, hoa phục sinh khỏi menu Trung Thu 2026.

## Thưởng và dữ liệu thiếu

Danh sách trong populateRewards() bám các nhóm phần thưởng của bài; vật phẩm có hằng số nhưng không có template thực tế sẽ tự bị loại. Những tên không có hằng số được tìm chính xác trong DB đã nạp; không tìm thấy thì bỏ qua. NPC Tiên Nữ có mục "Vật phẩm thiếu dữ liệu" để xem danh sách thực tế trên server.

Các tên tìm theo DB gồm chakra xanh/vàng, lồng đèn rồng, một số Minh Giác, bảo hiểm dung hợp, dung dịch cải tạo, IK, phiếu may mắn. Obito/Sakura luôn bị loại.

Bài không công bố xác suất, EXP, mức xu/yên hay hạn cụ thể từng món. Bản này KHÔNG khẳng định sao chép các giá trị đó:
- Trọng số nhóm thường 10, nhóm hiếm 1. Không phải 10%/1%; xác suất chia cho tổng trọng số của bảng sau khi lọc dữ liệu.
- Lồng đèn trong hộp cao cấp có 1/10.000 cơ hội vĩnh viễn khi đã chọn trúng món, đây là giá trị Kiss cấu hình.
- Bánh/hộp: 10–15 triệu EXP; hộ tống 15 triệu EXP trước hệ số EXP server. Thả đèn không thêm EXP. Xu/yên quay trúng: 1 triệu. Có thể cân chỉnh sau; EXP quái /5 vẫn giữ.
- Hạn thưởng dùng Item.initExpire của Kiss; lantern/mặt nạ Vegeta/Kunoichi/Hakairo/thỏ và đồ 10x thiếu initExpire được đặt 3 ngày. Không tự cho đồ thời hạn thành vĩnh viễn.
- Anti đồ sát khi nâng bằng lượng giữ xác suất Kiss 1/200, nay option giảm sát thương =100 (trước đó random1–30).

TOP hiện phát được phần có dữ liệu:
- TOP1/2: rương Huyền bí 3/2; gậy vĩnh viễn tự chọn; Hakairo Yoroi vĩnh viễn; đá danh vọng cấp2 300/200.
- TOP3–5: 2 rương Bạch ngân; Lân sư vũ/Bạch hổ 180 ngày; thời trang Trung Thu theo giới tính vĩnh viễn; lồng đèn thời trang với bộ option tròn/cá chép/ngôi sao/mặt trăng tự chọn; 200 đá danh vọng cấp2.
- TOP6–10: 2 Bát bảo; thú tự chọn 180 ngày; Ứng Long 90 ngày có giảm sát thương100%; 200 đá danh vọng cấp1.
- Bỏ quà TOP lồng đèn rồng và bí kíp 6x có thông số riêng vì chưa có cơ chế/option gốc tương ứng xác thực. Mốc hộp cao cấp cần mặt nạ 7 dòng+lồng đèn rồng nên chưa phát quà thay thế.
- Các hệ thống cải tạo/hóa hình/tách thời trang, BST Kim Cang Y và bật/tắt trang phục trong phần update chung của bài không được giả lập trong patch thưởng này.
- Source hiện không có chức năng gia hạn đồ nên chưa thêm chức năng gia hạn một lần hay phôi riêng cho nhóm 10x sự kiện.

## Obito / Sakura

SQL sao lưu item/store_data; gỡ hàng bán; đánh dấu template [REMOVED] và chặn dùng trong Char. Giữ ID, không xóa hàng template gây lệch ItemManager. Vật phẩm đã nằm trong túi/trang bị không tự bị xóa; cần sao lưu DB và thông tin chính xác trước khi dọn dữ liệu JSON nhân vật. Không đổi các mặt nạ Naruto khác như Tobi.

## Áp dụng

1. Dừng server sạch, backup DB.
2. git pull --ff-only origin main; build Maven.
3. Chạy sql/trung_thu_2026_go_bo_obito_sakura.sql trên DB game.
4. Trong file cấu hình đang dùng đặt game.event=com.nsoz.event.TrungThuNew. Không thay các thiết lập kết nối DB.
5. Chạy JAR mới, mở Tiên Nữ kiểm tra danh sách món bị bỏ qua; thử làm một bánh, hộp, thả/rước đèn.

## Kiểm chứng

Build: mvn -DskipTests package.
Kiểm tra độc lập dùng template giả, không kết nối DB/network:
java -cp target/test-classes:target/Nso-jar-with-dependencies.jar com.nsoz.event.TrungThu2026Check
Windows dùng dấu ; thay dấu : trong classpath.
Bao gồm tiền công/nguyên liệu, bảng thưởng riêng, cap bánh và JSON reload, mốc hộp, thứ tự TOP, giới hạn5.000, quà10x đủ11 dòng, chống nhận thưởng mốc/TOP hai lần. Không thay thế kiểm thử trực tiếp với DB/client thật.
