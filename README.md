<h1>ANKIDROID: BÀI TẬP LỚN MÔN OOP CỦA NHÓM 1</h1>
<h2>I. Giới thiệu tác giả:</h2>
<ul>
  <li>Đoàn Anh Minh - 24021565</li>
  <li>Hoàng Lâm Tùng - 24021661</li>
  <li>Nguyễn Xuân Tường - 24021669</li>
  <li>Doãn Duy Lợi - 24021549</li>
</ul>

<h2>II. Tổng quan về trò chơi Ankidroid</h2>
<ul>
  <li><b>Video demo game: </b><a href="https://www.youtube.com/watch?v=DJcHESalCps">Video</a></li>
  <li><b>Ngôn ngữ lập trình:</b> Java có kết hợp JavaFX library</li>
  <li><b>Tóm tắt:</b> Người chơi điều khiển paddle bằng chắn bóng sao cho bóng phá vỡ hết tất cả brick. Có 5 mạng, mỗi lần để rơi bóng thì sẽ mất 1 mạng. Nếu để rơi bóng 5 lần thì sẽ <b>thua cuộc</b>. Nếu phá hết tất cả brick thì <b>thắng cuộc</b>. Người chơi chỉ cần di chuột để điều khiển paddle.</li>
</ul>

<h2>III. Object</h2>
<h3>1. Ball</h3>
<p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/Breakout%20Tile%20Set%20Free/PNG/58-Breakout-Tiles.png" width=150></p>
<h3>2. Paddle</h3>
<p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/resources/paddle.png" width=300></p>
<h3>3. Block</h3>
<p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/resources/block.png" width=150></p>
<h3>4. Brick</h3>
Có 4 loại gạch, tương ứng 4 mức độ khác nhau. Cụ thể:
<ul>
  <li><b>Jewel brick:</b> Đây là loại gạch yếu nhất. Bóng chạm gạch thì gạch bị vỡ luôn<p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/Sprite_Bricks/Jewel/sprite.png" width=150></p></li>
  <p align="center"><em>Jewel brick</em></p>
  <li><b>Wood brick:</b> Bóng chạm 2 lần thì gạch mới bị vỡ<p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/Sprite_Bricks/Wood/Wood.png" width=150></p></li>
  <p align="center"><em>Wood brick</em></p>
  <li><b>Rock brick:</b> Bóng chạm 3 lần thì gạch bị vỡ<p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/Sprite_Bricks/Rock/Rock.png" width=150></p></li>
  <p align="center"><em>Rock brick</em></p>
  <li><b>Metal brick:</b> Đây là loại gạch cứng nhất. 4 lần bóng chạm vào thì gạch mới bị vỡ<p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/Sprite_Bricks/Metal/Iron.png" width=150></p></li>
  <p align="center"><em>Metal brick</em></p>
</ul>
<h3>5. Capsule</h3>
Có tất cả 18 loại. Capsule có tác dụng khi paddle <b>hứng được</b> capsule đó.
<ul>
  <li>Các loại capsule có ghi số với dấu +/- liền trước. Nếu là số dương thì điểm sẽ tăng một lượng được ghi trong capsule. Nếu số âm thì trừ điểm (điểm có thể là số âm). Dưới đây là một ví dụ:
  <p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/Image_Capsules/39-Breakout-Tiles.png" width=150></p></li>
  <p align="center"><em>Nếu paddle hứng được capsule này thì điểm sẽ tăng thêm 250</em></p>
  <li>Nới rộng paddle:
  <p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/Image_Capsules/expand_paddle_capsule.png" width=150></p></li>
  <li>Thu hẹp paddle:
  <p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/Image_Capsules/shrink_paddle_capsule.png" width=150></p></li>
  <li>Bóng đi nhanh hơn:
  <p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/Image_Capsules/fast_ball_capsule.png" width=150></p></li>
  <li>Bóng đi chậm hơn:
  <p align="center"><img src="https://github.com/gameGroup1/demo-repository/blob/main/Image_Capsules/slow_ball_capsule.png" width=150></p></li>
</ul>

<h2>IV. Cách tính điểm</h2>
Cứ phá được 1 brick thì điểm sẽ tăng thêm 10. Khi paddle hứng được capsule có chức năng cộng/trừ điểm thì điểm sẽ thay đổi một lượng được ghi trên capsule (điểm có thể âm). Điểm chính thức của trò chơi là điểm sau khi thua cuộc.
