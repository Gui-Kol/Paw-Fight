param([switch]$VerifyOnly)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @'
using System;
using System.Drawing;
using System.Drawing.Imaging;
public static class MenuPacking {
    public static Rectangle Bounds(Bitmap b) {
        int l=b.Width,t=b.Height,r=-1,d=-1;
        for(int y=0;y<b.Height;y++) for(int x=0;x<b.Width;x++) {
            if(b.GetPixel(x,y).A<128) continue;
            l=Math.Min(l,x);t=Math.Min(t,y);r=Math.Max(r,x);d=Math.Max(d,y);
        }
        if(r<l) throw new Exception("Empty sprite");
        return Rectangle.FromLTRB(l,t,r+1,d+1);
    }
    public static Bitmap Scale(Bitmap b,Rectangle src,int w,int h) {
        var result=new Bitmap(w,h,PixelFormat.Format32bppArgb);
        for(int y=0;y<h;y++) for(int x=0;x<w;x++) {
            int sx=src.X+Math.Min(src.Width-1,(int)((x+.5)*src.Width/w));
            int sy=src.Y+Math.Min(src.Height-1,(int)((y+.5)*src.Height/h));
            result.SetPixel(x,y,b.GetPixel(sx,sy));
        }
        return result;
    }
    public static void Paste(Bitmap canvas,Bitmap sprite,int dx,int dy) {
        for(int y=0;y<sprite.Height;y++) for(int x=0;x<sprite.Width;x++) canvas.SetPixel(dx+x,dy+y,sprite.GetPixel(x,y));
    }
    public static void Buttons(string input,string folder,string name,int w,int h) {
        using(var src=new Bitmap(input)) {
            for(int i=0;i<3;i++) {
                int y0=src.Height*i/3,y1=src.Height*(i+1)/3;
                using(var cell=src.Clone(new Rectangle(0,y0,src.Width,y1-y0),PixelFormat.Format32bppArgb))
                using(var logical=Scale(cell,Bounds(cell),name=="plus"?28:116,name=="plus"?28:52))
                using(var canvas=new Bitmap(w,h,PixelFormat.Format32bppArgb))
                using(var button=Scale(logical,new Rectangle(0,0,logical.Width,logical.Height),name=="plus"?56:464,name=="plus"?56:208)) {
                    Paste(canvas,button,(w-button.Width)/2,(h-button.Height)/2);
                    canvas.Save(System.IO.Path.Combine(folder,name+(i+1)+".png"),ImageFormat.Png);
                }
            }
        }
    }
    public static void Panel(string input,string folder) {
        using(var src=new Bitmap(input))
        using(var logical=Scale(src,Bounds(src),120,120))
        using(var full=Scale(logical,new Rectangle(0,0,120,120),480,480))
        using(var sheet=new Bitmap(3072,512,PixelFormat.Format32bppArgb)) {
            int[] heights={96,168,256,352,432,480};
            for(int i=0;i<6;i++) {
                using(var frame=new Bitmap(512,512,PixelFormat.Format32bppArgb))
                using(var panel=Scale(full,new Rectangle(0,0,480,480),480,heights[i])) {
                    Paste(frame,panel,16,(512-heights[i])/2);
                    Paste(sheet,frame,i*512,0);
                    if(i==5) frame.Save(System.IO.Path.Combine(folder,"fundoFinal.png"),ImageFormat.Png);
                }
            }
            sheet.Save(System.IO.Path.Combine(folder,"pauseFundo-Sheet.png"),ImageFormat.Png);
        }
    }
    public static void Check(string path,int w,int h) {
        using(var b=new Bitmap(path)) {
            if(b.Width!=w || b.Height!=h) throw new Exception("Wrong dimensions: "+path);
            bool clear=false,opaque=false;
            for(int y=0;y<h;y++) for(int x=0;x<w;x++) {
                int a=b.GetPixel(x,y).A;
                if(a==0) clear=true;
                if(a>=128) opaque=true;
            }
            if(!clear || !opaque) throw new Exception("Missing transparency or artwork: "+path);
        }
    }
    public static void CheckPanel(string folder) {
        using(var s=new Bitmap(System.IO.Path.Combine(folder,"pauseFundo-Sheet.png")))
        using(var f=new Bitmap(System.IO.Path.Combine(folder,"fundoFinal.png"))) {
            int last=0;
            for(int i=0;i<6;i++) {
                using(var frame=s.Clone(new Rectangle(i*512,0,512,512),PixelFormat.Format32bppArgb)) {
                    int height=Bounds(frame).Height;
                    if(height<=last) throw new Exception("Opening frames are not increasing");
                    last=height;
                }
            }
            for(int y=0;y<512;y++) for(int x=0;x<512;x++)
                if(s.GetPixel(2560+x,y).ToArgb()!=f.GetPixel(x,y).ToArgb()) throw new Exception("Final panel differs from last animation frame");
        }
    }
}
'@
$names=@('play','quit','save','resume','settings','plus')
$buttons=Join-Path $root 'assets/menu/button'
$pause=Join-Path $root 'assets/menu/pause'
if(-not $VerifyOnly) {
    foreach($name in $names) {
        $w=512; $h=270
        if($name -eq 'settings') {$h=300}
        if($name -eq 'plus') {$w=64;$h=64}
        [MenuPacking]::Buttons((Join-Path $PSScriptRoot "source/$name.png"),(Join-Path $buttons $name),$name,$w,$h)
        Get-ChildItem (Join-Path $buttons $name) -Filter '*.png' | Copy-Item -Destination (Join-Path $root "assets/ui/buttons/$name")
    }
    [MenuPacking]::Panel((Join-Path $PSScriptRoot 'source/pause.png'),$pause)
    Get-ChildItem $pause -Filter '*.png' | Copy-Item -Destination (Join-Path $root 'assets/ui/pause')
}
foreach($name in $names) {
    $w=512;$h=270
    if($name -eq 'settings') {$h=300}
    if($name -eq 'plus') {$w=64;$h=64}
    $hashes=@()
    for($i=1;$i -le 3;$i++) {
        $file="$name/$name$i.png"
        $path=Join-Path $buttons $file
        [MenuPacking]::Check($path,$w,$h)
        $hash=(Get-FileHash $path).Hash
        if($hash -ne (Get-FileHash (Join-Path $root "assets/ui/buttons/$file")).Hash) {throw "Duplicate differs: $file"}
        $hashes+=$hash
    }
    if(($hashes | Select-Object -Unique).Count -ne 3) {throw "Button states must be distinct: $name"}
    Write-Output "$name : three distinct states, dimensions, alpha and mirrored copies OK"
}
foreach($spec in @(@('fundoFinal.png',512,512),@('pauseFundo-Sheet.png',3072,512))) {
    $path=Join-Path $pause $spec[0]
    [MenuPacking]::Check($path,$spec[1],$spec[2])
    if((Get-FileHash $path).Hash -ne (Get-FileHash (Join-Path $root ('assets/ui/pause/'+$spec[0]))).Hash) {throw 'Pause copy differs'}
}
[MenuPacking]::CheckPanel($pause)
Write-Output 'Pause: six increasing opening frames, identical final panel, alpha and mirrored copies OK'

$preview=[Drawing.Bitmap]::new(1200,950)
$g=[Drawing.Graphics]::FromImage($preview)
$g.Clear([Drawing.ColorTranslator]::FromHtml('#111827'))
$font=[Drawing.Font]::new('Consolas',12)
$brush=[Drawing.SolidBrush]::new([Drawing.ColorTranslator]::FromHtml('#e5e7eb'))
function Draw-Export($path,$x,$y,$w,$h) {
    $img=[Drawing.Bitmap]::new($path)
    try {
        $scaled=[MenuPacking]::Scale($img,[Drawing.Rectangle]::new(0,0,$img.Width,$img.Height),$w,$h)
        try {$g.DrawImageUnscaled($scaled,[int]$x,[int]$y)} finally {$scaled.Dispose()}
    } finally {$img.Dispose()}
}
try {
    $g.DrawString('PAWFIGHT / BUTTONS + PAUSE',$font,$brush,24,20)
    $g.DrawString('NORMAL',$font,$brush,24,60)
    $g.DrawString('HOVER',$font,$brush,224,60)
    $g.DrawString('PRESSED',$font,$brush,424,60)
    for($row=0;$row -lt $names.Count;$row++) {
        $name=$names[$row]
        for($i=1;$i -le 3;$i++) {
            $w=184;$h=97
            if($name -eq 'plus') {$w=64;$h=64}
            Draw-Export (Join-Path $buttons "$name/$name$i.png") (24+($i-1)*200) (90+$row*108) $w $h
        }
    }
    $g.DrawString('PAUSE / EXISTING GAME LAYOUT',$font,$brush,660,60)
    Draw-Export (Join-Path $pause 'fundoFinal.png') 660 90 512 512
    $row=0
    foreach($name in @('resume','save','settings','quit')) {
        Draw-Export (Join-Path $buttons "$name/${name}1.png") 866 (90+80+$row*75) 100 52
        $row++
    }
    $g.DrawString('OPENING / 6 FRAMES',$font,$brush,24,770)
    Draw-Export (Join-Path $pause 'pauseFundo-Sheet.png') 24 800 900 150
    $preview.Save((Join-Path $PSScriptRoot 'preview.png'),[Drawing.Imaging.ImageFormat]::Png)
} finally {$g.Dispose();$preview.Dispose();$font.Dispose();$brush.Dispose()}
