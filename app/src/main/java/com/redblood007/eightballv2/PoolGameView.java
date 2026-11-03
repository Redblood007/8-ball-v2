package com.redblood007.eightballv2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class PoolGameView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Ball> balls = new ArrayList<>();
    private float left, top, right, bottom, r;
    private Ball cue;
    private boolean aiming = true, moving = false;
    private float aimX, aimY, power = 0.55f;
    private long last;

    private static class Ball { float x,y,vx,vy; int color,number; boolean pocketed; Ball(float x,float y,int c,int n){this.x=x;this.y=y;color=c;number=n;} }

    public PoolGameView(android.content.Context c) { super(c); p.setStrokeCap(Paint.Cap.ROUND); setFocusable(true); reset(); }

    private void reset(){ balls.clear(); cue=new Ball(0,0,Color.WHITE,0); balls.add(cue);
        int[] colors={0xFFFFD21C,0xFF1976D2,0xFFE53935,0xFF8E44AD,0xFFFF8F00,0xFF2E7D32,0xFF8E1B1B,0xFF111111,0xFF111111,0xFFFFD21C,0xFF1976D2,0xFFE53935,0xFF8E44AD,0xFFFF8F00,0xFF2E7D32};
        for(int i=0;i<15;i++) balls.add(new Ball(0,0,colors[i],i+1));
        last=System.nanoTime(); postInvalidate(); }

    @Override protected void onSizeChanged(int w,int h,int ow,int oh){ layoutTable(w,h); }
    private void layoutTable(int w,int h){ float margin=w*0.045f; left=margin; right=w-margin; float tableH=Math.min(h*0.62f,w*0.62f); top=h*0.18f; bottom=top+tableH; r=Math.max(10,Math.min(w,h)*0.027f);
        cue.x=left+(right-left)*0.24f; cue.y=(top+bottom)/2;
        int k=1; float bx=left+(right-left)*0.72f, by=(top+bottom)/2;
        for(int row=0;row<5;row++) for(int j=0;j<=row;j++){ Ball b=balls.get(k++); b.x=bx+row*r*1.75f; b.y=by+(j-row/2f)*r*2.08f; }
        aimX=cue.x+150; aimY=cue.y; }

    @Override protected void onDraw(Canvas c){ super.onDraw(c); float w=getWidth(),h=getHeight();
        c.drawColor(Color.rgb(7,9,17));
        p.setStyle(Paint.Style.FILL); p.setColor(0xFF151A2B); c.drawRoundRect(new RectF(14,14,w-14,top-12),28,28,p);
        p.setColor(Color.WHITE); p.setTextSize(22); p.setTypeface(android.graphics.Typeface.DEFAULT_BOLD); c.drawText("8 BALL POOL",30,48,p);
        p.setTextSize(14); p.setTypeface(android.graphics.Typeface.DEFAULT); c.drawText("PRACTICE",30,70,p);
        p.setColor(0xFF20263A); c.drawRoundRect(new RectF(w-130,27,w-28,72),22,22,p); p.setColor(0xFFFFD54F); c.drawText("●  1,250",w-113,55,p);

        p.setColor(0xFF5B321F); c.drawRoundRect(new RectF(left-18,top-18,right+18,bottom+18),22,22,p);
        p.setColor(0xFF0B6B45); c.drawRoundRect(new RectF(left,top,right,bottom),10,10,p);
        p.setColor(0xFF073D2B); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(2); c.drawRect(left,top,right,bottom,p); p.setStyle(Paint.Style.FILL);
        float[] px={left,right,(left+right)/2,(left+right)/2,left,right}; float[] py={top,top,top,bottom,bottom,bottom};
        for(int i=0;i<6;i++){p.setColor(Color.BLACK); c.drawCircle(px[i],py[i],r*1.35f,p);}

        for(Ball b:balls) if(!b.pocketed) drawBall(c,b);
        if(aiming && !moving && cue!=null && !cue.pocketed){
            p.setColor(0xAAFFFFFF); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(3); c.drawLine(cue.x,cue.y,aimX,aimY,p); p.setStyle(Paint.Style.FILL);
            p.setColor(0x66FFFFFF); c.drawCircle(aimX,aimY,r*0.7f,p);
        }
        p.setColor(0xFF171D30); c.drawRoundRect(new RectF(20,bottom+28,w-20,h-24),28,28,p);
        p.setColor(Color.WHITE); p.setTextSize(15); c.drawText("AIM",40,bottom+60,p);
        p.setColor(0xFF2A324A); c.drawRoundRect(new RectF(95,bottom+48,w-145,bottom+60),6,6,p); p.setColor(0xFF61E6B2); c.drawRoundRect(new RectF(95,bottom+48,95+(w-240)*power,bottom+60),6,6,p);
        p.setColor(Color.WHITE); c.drawText("POWER",w-125,bottom+60,p);
        p.setColor(0xFF61E6B2); c.drawCircle(w-65,bottom+54,24,p); p.setColor(0xFF07130F); p.setTextSize(12); c.drawText("SHOOT",w-88,bottom+58,p);
        p.setColor(0xFF9AA4BC); p.setTextSize(13); c.drawText(moving?"Balls moving…":"Drag to aim • tap SHOOT",30,h-10,p);
        if(moving){ long now=System.nanoTime(); float dt=Math.min(0.025f,(now-last)/1e9f); last=now; step(dt); postInvalidateDelayed(16); }
    }

    private void drawBall(Canvas c,Ball b){ p.setStyle(Paint.Style.FILL); p.setColor(b.color); c.drawCircle(b.x,b.y,r,p); if(b.number>0){p.setColor(Color.WHITE); c.drawCircle(b.x,b.y,r*0.42f,p); p.setColor(Color.BLACK); p.setTextSize(r*0.62f); p.setTextAlign(Paint.Align.CENTER); c.drawText(""+b.number,b.x,b.y+r*0.22f,p); p.setTextAlign(Paint.Align.LEFT);} p.setColor(0x55FFFFFF); c.drawCircle(b.x-r*.25f,b.y-r*.25f,r*.18f,p); }

    private void step(float dt){ boolean any=false; float friction=(float)Math.pow(0.985,dt*60); for(Ball b:balls) if(!b.pocketed){ b.x+=b.vx*dt; b.y+=b.vy*dt; b.vx*=friction; b.vy*=friction; if(Math.hypot(b.vx,b.vy)>8) any=true;
            if(b.x<left+r){b.x=left+r;b.vx=Math.abs(b.vx)*0.92f;} if(b.x>right-r){b.x=right-r;b.vx=-Math.abs(b.vx)*0.92f;} if(b.y<top+r){b.y=top+r;b.vy=Math.abs(b.vy)*0.92f;} if(b.y>bottom-r){b.y=bottom-r;b.vy=-Math.abs(b.vy)*0.92f;}
            float[][] pockets={{left,top},{right,top},{left,bottom},{right,bottom},{(left+right)/2,top},{(left+right)/2,bottom}}; for(float[] q:pockets) if(Math.hypot(b.x-q[0],b.y-q[1])<r*1.7f){b.pocketed=true;b.vx=b.vy=0;}
        }
        for(int i=0;i<balls.size();i++) for(int j=i+1;j<balls.size();j++){Ball a=balls.get(i),b=balls.get(j); if(a.pocketed||b.pocketed)continue; float dx=b.x-a.x,dy=b.y-a.y,d=(float)Math.hypot(dx,dy),min=2*r; if(d>0&&d<min){float nx=dx/d,ny=dy/d;float rv=(b.vx-a.vx)*nx+(b.vy-a.vy)*ny;if(rv<0){float imp=-rv; a.vx-=imp*nx;a.vy-=imp*ny;b.vx+=imp*nx;b.vy+=imp*ny;}float push=(min-d)/2;a.x-=nx*push;a.y-=ny*push;b.x+=nx*push;b.y+=ny*push;}}
        if(!any){moving=false;aiming=true; if(cue.pocketed){cue.pocketed=false;cue.x=left+(right-left)*0.24f;cue.y=(top+bottom)/2;}}
    }

    @Override public boolean onTouchEvent(MotionEvent e){float x=e.getX(),y=e.getY(); if(e.getAction()==MotionEvent.ACTION_DOWN){ if(moving)return true; if(x>getWidth()-125&&y>bottom+20){shoot();return true;} if(y>=top&&y<=bottom){aimX=x;aimY=y;aiming=true;return true;} }
        if(e.getAction()==MotionEvent.ACTION_MOVE&&!moving){aimX=x;aimY=y; invalidate();} return true; }
    private void shoot(){float dx=aimX-cue.x,dy=aimY-cue.y;float d=(float)Math.hypot(dx,dy);if(d<8)return;float s=900*power;cue.vx=dx/d*s;cue.vy=dy/d*s;moving=true;aiming=false;last=System.nanoTime();invalidate();}
}
