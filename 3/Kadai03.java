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

//図形描画に必要なクラス
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Kadai03 extends Application {

	public static void main(String[] args){
        // メインメソッドから実行
        Application.launch(args);
 
    }
	
	//原画像の濃度値配列
    @Override
    public void start(Stage stage) throws Exception {
		Image img = new Image( "kousha2.jpg" );		//画像ファイルロード
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
    	Text title0 = new Text("画像処理名:ヒストグラム");	//① 上部タイトルを(0,0)に配置。引数は 列,行 の順に指定。
    	grid.add(title0,0,0);
    	//① (1,0)にタイトル「原画像」を、その下(2,0)に原画像を表示
    	Text title1 = new Text("原画像");
    	grid.add(title1,0,1);
    	grid.add(imgView1,0,2);							
    	//① (1,1)にタイトル「処理画像」を、その下(2,1)に処理画像を表示
    	Text title2 = new Text("処理画像");
    	grid.add(title2,1,1);
        grid.add(imgView2,1,2);
    	
    	
    	//***** 原画像ヒストグラムの描画処理 *****
    	int[] gengaHist = getHistArray(aryKido, width, height);	//(1)仮のヒストグラム配列の取得。画像の濃度値配列を関数に渡し、ヒストグラム配列を受け取るようにする。
    	Canvas gengaCanvas = drawChart(gengaHist,256,200);		//(2)ヒストグラム配列を基に、グラフを描画したCanvasを作成する。2つの引数は描画するグラフのサイズである。
    	grid.add(gengaCanvas,0,3);								//(3)GridPaneの3行0列に原画像のCanvasインスタンスを追加
    	
    	//***** 処理画像ヒストグラムの描画処理 *****
    	int[] shoriHist = getHistArray(aryProcKido, width, height);
		Canvas shoriCanvas = drawChart(shoriHist,256,200);
		grid.add(shoriCanvas,1,3);

    	//原画像の処理を参考に自分で実装する。
    	
    	Scene scene = new Scene(grid, 1000, 650);	//②
 
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
	 ** ヒストグラム描画のためのメソッド  		                           **
	 ** ヒストグラム配列を基に指定サイズで描画したCanvasを返す             **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/
	public Canvas drawChart(int[] hist, double width, double height){

		double max=0;
		for(int j=0;j<256;j++)
		{
			if(max<=(double)hist[j])
			{
				max=(double)hist[j];
			}
		}
		double L=0;
		
		Canvas canvas = new Canvas(width, height);				//Canvasを幅width、高さheightで生成
		GraphicsContext gc  = canvas.getGraphicsContext2D();	//Canvasへ描画するためのインスタンスを生成
		
		gc.setFill(Color.rgb(97, 150, 242));	//塗りつぶしの色をRGBで指定
		gc.fillRect(0, 0, width, height);		//Canvas左上座標(0,0)から幅widht×高さheightの塗りつぶされた長方形を描画

		gc.setStroke(Color.rgb(255, 0, 0));		//線の色を指定
		for(int i=0;i<256;i++)
		{
			L=(hist[i]/max)*height;
			gc.strokeLine(i, height-1,i, height-L-1);
		}
		
		return canvas;		//描画が完了したCanvasを返す。
	}	
	
	/************************************************************************
	 ************************************************************************
	 **                                                                    **
	 ** ヒストグラム用配列のためのメソッド  		                       **
	 ** 濃度値配列を基に、1次元のヒストグラム配列を返す                    **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/
	public int[] getHistArray(int[][] aryKido, int width, int height){
		
		
		//ここにヒストグラム濃度値を要素番号、その濃度値の画素数を要素の値とする配列histを作成するように実装する。
		//これは、濃度値0の画素が5個、濃度値1が100個、濃度値2が50個を示す。aryKidoをもとに生成する。
		int[] hist=new int[256];
		for(int i=0;i<width;i++)
		{
			for(int j=0;j<height;j++)
			{
				hist[aryKido[i][j]]++;
			}
		}	
		
		return hist;
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

		//2重のforで1画素ずつ処理を行う。
		for(int y=0; y < height; y++ ){
			for(int x=0; x < width; x++ ){
				
				aryProcKido[x][y] = aryKido[x][y];
			}
		}

		//LUTの生成
		
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
