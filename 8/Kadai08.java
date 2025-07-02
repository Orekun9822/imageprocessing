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

public class Kadai08 extends Application {

    //原画像の濃度値配列
    @Override
    public void start(Stage stage) throws Exception {
        //ターゲット画像ファイルのロード
        Image img1 = new Image( "kousha.jpg" );
        int width1 = (int) img1.getWidth();
        int height1= (int) img1.getHeight();


        //輝度値のみの配列を取得
        int aryKido1[][] = getLuminanceArray(img1, width1, height1);         //原画像の輝度値配列を取得

        WritableImage gImg1 = drawGrayImage(aryKido1, width1, height1);     //原画像をグレイスケールに変換した描画用インスタンスの取得

        //2枚めのテンプレート画像をロード
        Image img2 = new Image( "small.jpg" );
        int width2 = (int) img2.getWidth();
        int height2= (int) img2.getHeight();
        int aryKido2[][] = getLuminanceArray(img2, width2, height2);        //画像処理後の輝度値配列を取得

        WritableImage gImg2 = drawGrayImage(aryKido2, width2, height2); //画像処理適用後の描画用インスタンスの取得

        ImageView imgView1 = new ImageView( gImg1 );    //ImageVew生成
        ImageView imgView2 = new ImageView( gImg2 );    //ImageVew生成
        GridPane grid = new GridPane();                  //SceneGraphのインスタンス生成（GridPane）
        //grid.setGridLinesVisible(true);                   //デバッグ用にセルの線を表示
        grid.setAlignment(Pos.CENTER);                     //フォーム中央に配置
        grid.setHgap(10);                                  //各セルの左右間のギャップを10ピクセルに設定
        grid.setVgap(10);                                  //各セルの上下間のギャップを10ピクセルに設定

        // --- テンプレートマッチング処理 ---
        double maxZNCC = -1.0; // ZNCCは-1から1の範囲なので、初期値は-1.0
        int bestX = -1;
        int bestY = -1;

        // 探索範囲は、ターゲット画像内でテンプレート画像がはみ出さない範囲
        for (int y = 0; y <= height1 - height2; y++) {
            for (int x = 0; x <= width1 - width2; x++) {
                double currentZNCC = calcZNCC(aryKido1, width1, height1, x, y, aryKido2, height2, width2);

                if (currentZNCC > maxZNCC) {
                    maxZNCC = currentZNCC;
                    bestX = x;
                    bestY = y;
                }
            }
        }

        System.out.println("R_ZNCCが最大となるkousha.jpg上の座標(x,y) = (" + bestX + ", " + bestY + ")");
        System.out.println("R_ZNCCの値 = " + maxZNCC);

        //gridpaneの縦横セル数は自動的に指定した位置を基に最小限に自動決定される
        Text title0 = new Text("画像処理名:テンプレートマッチング\nR_ZNCC＝ " + String.format("%.4f", maxZNCC) + "\n座標(x,y)=(" + bestX + ", " + bestY + ")");  //① 上部タイトルを(0,0)に配置。引数は 列,行 の順に指定。
        grid.add(title0,0,0);
        //① (1,0)にタイトル「原画像」を、その下(2,0)に原画像を表示
        Text title1 = new Text("テンプレート画像");
        grid.add(title1,0,1);
        grid.add(imgView2,0,2); // imgView2 is small.jpg
        //① (1,1)にタイトル「処理画像」を、その下(2,1)に処理画像を表示
        Text title2 = new Text("入力画像");
        grid.add(title2,1,1);
        grid.add(imgView1,1,2); // imgView1 is kousha.jpg
        Scene scene = new Scene(grid, 1000, 500);   //②

        //CSSによる見た目の設定
        scene.getStylesheets().add("style.css");            //cssファイル読み込み
        title1.getStyleClass().add("my-title1");            //classの設定
        title2.setId("my-title2");                            //idの設定

        stage.setScene(scene);                         //③
        stage.show();                                  //④
    }


    /***************************************************************************
     * ZNCC計算関数
     * 参考まに関数を書いた。こちらに書き足しても良いし、自分で変更しても良い。
     *
     * ■引数
     * targetImage：探索対象の画像の画素値配列
     * targetWidth, targetHeight：探索対象画像の高さ、幅
     * startX, startY：znccを計算するテンプレート画像(0,0）のtargetImage上の座標(startX,startY)
     * templateImage：テンプレート画像の画素値配列
     * templateHeight, templateWidth：テンプレート画像の高さ、幅
     *
     * ■戻り値
     * zncc：（startX,startY）上でのznccの値
     **************************************************************************/
    public double calcZNCC(int[][] targetImage, int targetWidth, int targetHeight, int startX, int startY, int[][] templateImage, int templateHeight, int templateWidth) {

        double sumF = 0;
        double sumG = 0;

        // Calculate mean of target window (f_bar)
        for (int y = 0; y < templateHeight; y++) {
            for (int x = 0; x < templateWidth; x++) {
                sumF += targetImage[startX + x][startY + y];
            }
        }
        double meanF = sumF / (templateWidth * templateHeight);

        // Calculate mean of template (g_bar)
        for (int y = 0; y < templateHeight; y++) {
            for (int x = 0; x < templateWidth; x++) {
                sumG += templateImage[x][y];
            }
        }
        double meanG = sumG / (templateWidth * templateHeight);

        double numerator = 0;
        double sumFSquaredDiff = 0; // Sum of (f(x,y) - f_bar)^2
        double sumGSquaredDiff = 0; // Sum of (g(x,y) - g_bar)^2

        for (int y = 0; y < templateHeight; y++) {
            for (int x = 0; x < templateWidth; x++) {
                double diffF = targetImage[startX + x][startY + y] - meanF;
                double diffG = templateImage[x][y] - meanG;

                numerator += diffF * diffG;
                sumFSquaredDiff += diffF * diffF;
                sumGSquaredDiff += diffG * diffG;
            }
        }

        double denominator = Math.sqrt(sumFSquaredDiff * sumGSquaredDiff);

        if (denominator == 0) {
            return 0; // Avoid division by zero, or handle as appropriate for ZNCC (e.g., return 1 if numerator is also 0)
        }

        double zncc = numerator / denominator;
        return zncc;
    }


    /************************************************************************
     ************************************************************************
     ** **
     ** 課題に応じた処理を記載するメソッド                                  **
     ** グレースケール画像の濃度値配列配列cpyKidoを基に                   **
     ** 処理結果の輝度値配列aryProcKidoを生成して返す                       **
     ** **
     ************************************************************************
     ************************************************************************/
    public int[][] imageProcessing(int[][] aryKido, int width, int height ) {

        int aryProcKido[][] = new int[width][height];

        // This method is not explicitly used for the template matching itself,
        // but it's part of the provided structure. For template matching,
        // we directly work with aryKido1 and aryKido2.
        // Copying the image as a placeholder.
        for(int y=0; y < height; y++ ){
            for(int x=0; x < width; x++ ){

                aryProcKido[x][y] = aryKido[x][y];
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