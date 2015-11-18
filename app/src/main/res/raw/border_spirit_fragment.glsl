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


void main() {
    vec4 colorBorder = step(uBorderEdge.x,(0.5-abs(vTextureCoord.x - 0.5)))
        * step(uBorderEdge.y,(0.5-abs(vTextureCoord.y - 0.5)))
        *  texture2D(uTexBorder,vBorderCoord);

    vec4 colorImage =  step(uImageEdge.x,(0.5-abs(vTextureCoord.x - 0.5)))
        * step(uImageEdge.y,(0.5-abs(vTextureCoord.y - 0.5)))
        *  texture2D(uTexBorder,vImageCoord);

    vec4 colorContent =  step(uContentEdge.x,(0.5-abs(vContentCoord.x - 0.5)))
        * step(uContentEdge.y,(0.5-abs(vContentCoord.y - 0.5)))
        *  texture2D(uTexContent,vContentCoord);


    if(length(colorImage.rgb)== 0.0 &&
     length(colorBorder.rgb)==0.0 &&
     length(colorContent.rgb)==0.0){
        gl_FragColor = vec4(0.0,0.0,0.0,0.0);
    }
    else if(length(colorImage.rgb)== 0.0 && length(colorBorder.rgb)!=0.0){
        gl_FragColor = colorBorder;
    }
    else if(length(colorImage.rgb)!= 0.0 && length(colorBorder.rgb)==0.0){
        gl_FragColor = colorImage;
    }
    else{

    vec4 tempColor = mix(colorImage,colorBorder,colorBorder.a);
        gl_FragColor =  mix(tempColor,colorContent,colorContent.a);
    }

}