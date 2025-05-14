import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class Kadai05_median extends Application {
	
	public static void main(String[] args){
 
        // メインメソッドから実行
        Application.launch(args);
 
    }
	
	//原画像の濃度値配列
    @Override
    public void start(Stage stage) throws Exception {
		Image img = new Image("yakei_noise.bmp");		//画像ファイルロード
    	int width = (int) img.getWidth();
        int height= (int) img.getHeight();
    	
    	
		//輝度値のみの配列を取得
		int aryKido[][] = getLuminanceArray(img, width, height);			//原画像の輝度値配列を取得
    	
    	WritableImage gImg1 = drawGrayImage(aryKido, width, height);		//原画像をグレイスケールに変換した描画用インスタンスの取得
		
    	//画像処理の適用
    	int aryProcKido[][] = imageProcessing(aryKido, width, height);		//画像処理後の輝度値配列を取得
    	
    	WritableImage gImg2 = drawGrayImage(aryProcKido, width, height);	//画像処理適用後の描画用インスタンスの取得
    	
		ImageView imgView1 = new ImageView( gImg1 );	//ImageVew生成
		ImageView imgView2 = new ImageView( gImg2 );	//ImageVew生成
		GridPane grid = new GridPane();					//SceneGraphのインスタンス生成（GridPane）
    	//grid.setGridLinesVisible(true);				//デバッグ用にセルの線を表示
    	grid.setAlignment(Pos.CENTER);					//フォーム中央に配置
    	grid.setHgap(10);								//各セルの左右間のギャップを10ピクセルに設定
		grid.setVgap(10);								//各セルの上下間のギャップを10ピクセルに設定

    	//gridpaneの縦横セル数は自動的に指定した位置を基に最小限に自動決定される
    	Text title0 = new Text("画像処理名:中央値フィルタ");	//① 上部タイトルを(0,0)に配置。引数は 列,行 の順に指定。
    	grid.add(title0,0,0);
    	//① (1,0)にタイトル「原画像」を、その下(2,0)に原画像を表示
    	Text title1 = new Text("原画像");
    	grid.add(title1,0,1);
    	grid.add(imgView1,0,2);							
    	//① (1,1)にタイトル「処理画像」を、その下(2,1)に処理画像を表示
    	Text title2 = new Text("処理画像");
    	grid.add(title2,1,1);
        grid.add(imgView2,1,2);
    	Scene scene = new Scene(grid, 1000, 500);	//②
 
    	//CSSによる見た目の設定
    	scene.getStylesheets().add("style.css");			//cssファイル読み込み
    	title1.getStyleClass().add("my-title1");			//classの設定
    	title2.setId("my-title2");							//idの設定
    	
    	stage.setScene(scene);						//③
        stage.show();								//④
    }

	/************************************************************************
	 ************************************************************************
	 **                                                                    **
	 ** 課題に応じた処理を記載するメソッド                                 **
	 ** グレースケール画像の濃度値配列配列cpyKidoを基に                    **
	 ** 処理結果の輝度値配列aryProcKidoを生成して返す                      **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/
	public int[][] imageProcessing(int[][] aryKido, int width, int height ) {

         int aryProcKido[][] = new int[width][height];

         for(int y=0; y < height; y++){
             for(int x=0; x < width; x++){
                 if (x > 0 && x < width - 1 && y > 0 && y < height - 1) {
                     int median[] = new int [9];
                     median[0] = aryKido[x-1][y-1];
                     median[1] = aryKido[x][y-1];
                     median[2] = aryKido[x+1][y-1];
                     median[3] = aryKido[x-1][y];
                     median[4] = aryKido[x][y];
                     median[5] = aryKido[x+1][y];
                     median[6] = aryKido[x-1][y+1];
                     median[7] = aryKido[x][y+1];
                     median[8] = aryKido[x+1][y+1];
                     //中央値を求める（バブルソート）
                     for(int i=0; i < median.length-1; i++){
                         for(int j=i+1; j < median.length; j++){
                             if(median[i] > median[j]){
                                 int temp = median[i];
                                 median[i] = median[j];
                                 median[j] = temp;
                             }
                         }
                     }
                     //中央値を格納する
                     aryProcKido[x][y] = median[4];
                 } else {
                     // 端のピクセルは元の値をそのまま格納
                     aryProcKido[x][y] = aryKido[x][y];
                 }
             }
         }
         return aryProcKido;
     }
	
	/************************************************************************
	 * 
	 * カラー画像からグレースケール画像を描画得するメソッド
	 * 求めた輝度値は濃度値配列配列aryKidoに格納する
	 *
	 ************************************************************************/
	public WritableImage drawGrayImage(int aryKido[][], int width, int height ) {
		WritableImage wImg = new WritableImage(width, height);
		PixelWriter writer = wImg.getPixelWriter();

		for(int y=0; y < height; y++ ){
			for(int x=0; x < width; x++ ){
				int kido = aryKido[x][y];
				
				//透過度は255固定（透過なし）
				writer.setArgb( x , y, createArgb(255, kido, kido, kido));
			}
		}
		
		return wImg;
	}

	/************************************************************************
	 * 
	 * カラー画像の各画素の濃度値から輝度値を求めるメソッド。
	 * 戻り値は輝度値を格納した各画素の濃度値配列
	 *
	 ************************************************************************/
	public int[][] getLuminanceArray(Image img, int width, int height) {
		//カラー画像各画素の濃度値を読み取るため
		PixelReader reader = img.getPixelReader();
				
		//グレイスケール画像の濃度値値を格納する配列
		int kidoGaso[][] = new int[width][height];
		
		//各画素の濃度値のα値、R、G、Bを格納する変数
		int argb, alpha, red, green, blue;
		
		// 1画素ずつRGBの値から輝度値を求める。
		for(int y=0; y < height; y++){
			for(int x=0; x < width; x++){
				//論理積をとることで、シフト後の下位8bitをそのまま残し、上位24bitは0とする。
				argb = reader.getArgb( x, y);
				alpha = (argb >> 24) & 0xFF;
				red   = (argb >> 16) & 0xFF;
				green = (argb >>  8) & 0xFF;
				blue  =  argb        & 0xFF;
				kidoGaso[x][y] = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
			}
		}
		
		return kidoGaso;
	}
	
	/************************************************************************
	 *
	 * a,r,g,bから32bitの濃度値を表すargbを生成する。
	 *
	 ************************************************************************/
	public int createArgb(int a, int r, int g, int b){
		int argb = (a << 24) | (r << 16 ) | ( g << 8 ) | b;
		
		return argb;
	}
	
}
