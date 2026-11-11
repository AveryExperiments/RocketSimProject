
public class RocketConstants {

    int weight;
    int height;
    int width;
    int fuel;

    public enum rocketModel {
        Tomahawk("/home/kasm-user/RocketProject/RocketSimProject/Assets/Tomahawk.png", 1.0, 0.05, 0.5, 0.45),
        Alpha3("/home/kasm-user/RocketProject/RocketSimProject/Assets/Alpha3.png", 0.312, 0.0254, 0.7, 0.60);

        public final String imgPath;
        public final double height;
        public final double width;
        public final double weight;
        public final double cd;

        rocketModel(String imgPath, double height, double width, double weight, double cd) {
            this.imgPath = imgPath;
            this.height = height;
            this.width = width;
            this.weight = weight;
            this.cd = cd;
        }

        rocketModel(String imgPath, double height, double width, double weight) {
            this(imgPath, height, width, weight, 0.5);
        }

    }

}
