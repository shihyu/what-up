precision mediump float;

uniform sampler2D uTexBorder;
uniform sampler2D uTexImage;
uniform sampler2D uTexContent;

uniform vec2 uBorderEdge;
uniform vec2 uImageEdge;
uniform vec2 uContentEdge;

varying vec2 vBorderCoord;
varying vec2 vImageCoord;
varying vec2 vContentCoord;

varying vec4 vColor;

void main() {

    vec4 colorBorder = step(uBorderEdge.x,(0.5-abs(vBorderCoord.x - 0.5)))
    * step(uBorderEdge.y,(0.5-abs(vBorderCoord.y - 0.5)))
    *  texture2D(uTexBorder,vBorderCoord);

    vec4 colorImage =  step(uImageEdge.x,(0.5-abs(vImageCoord.x - 0.5)))
    * step(uImageEdge.y,(0.5-abs(vImageCoord.y - 0.5)))
    *  texture2D(uTexImage,vImageCoord);

    vec4 colorContent =  step(uContentEdge.x,(0.5-abs(vContentCoord.x - 0.5)))
    * step(uContentEdge.y,(0.5-abs(vContentCoord.y - 0.5)))
    *  texture2D(uTexContent,vContentCoord);

    if(length(colorImage.rgb) == 0.0 ){
        colorImage.a = 0.0;
    }

    if(length(colorBorder.rgb) == 0.0 ){
        colorBorder.a =0.0;
    }

    if(length(colorContent.rgb) == 0.0 ){
        colorContent.a =0.0;
    }

    vec4 tempColor = mix(colorImage,colorBorder,colorBorder.a);

     gl_FragColor =  mix(tempColor,colorContent,colorContent.a);

}

