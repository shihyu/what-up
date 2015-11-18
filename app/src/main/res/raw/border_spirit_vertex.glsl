uniform mat4 uMVPMatrix;

attribute vec2 aEdge;
attribute vec2 aBorderCoord;
attribute vec2 aImageCoord;
attribute vec2 aContentCoord;

attribute vec4 aPosition;

varying vec2 vBorderCoord;
varying vec2 vImageCoord;
varying vec2 vContentCoord;

void main() {

    vBorderCoord = aBorderCoord;
    vImageCoord = aImageCoord;
    vContentCoord = aContentCoord;
    gl_Position = uMVPMatrix * aPosition;

}