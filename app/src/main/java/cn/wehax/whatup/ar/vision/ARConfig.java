package cn.wehax.whatup.ar.vision;

/**
 * Created by mayuhan on 15/7/31.
 */
public class ARConfig {
    final float nearZ;
    final float farZ;
    final float cameraZ;

    private ARConfig(Builder builder) {
        nearZ = builder.nearZ;
        farZ = builder.farZ;
        cameraZ = builder.cameraZ;
    }

    public static class Builder {
        private final float nearZ;
        private final float farZ;
        private final float cameraZ;

        public Builder(float nearZ, float farZ, float cameraZ) {
            this.nearZ = nearZ;
            this.farZ = farZ;
            this.cameraZ = cameraZ;
        }

        public ARConfig build() {
            return new ARConfig(this);
        }
    }
}
