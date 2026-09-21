param([switch]$VerifyOnly)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @'
using System;
using System.Drawing;
using System.Drawing.Imaging;
public static class HudAssetPacking {
    public static Rectangle Bounds(Bitmap b) {
        int left=b.Width, top=b.Height, right=-1, bottom=-1;
        for(int y=0;y<b.Height;y++) for(int x=0;x<b.Width;x++) {
            if(b.GetPixel(x,y).A < 128) continue;
            left=Math.Min(left,x); top=Math.Min(top,y);
            right=Math.Max(right,x); bottom=Math.Max(bottom,y);
        }
        if(right<left) throw new Exception("Empty generated sprite");
        return Rectangle.FromLTRB(left,top,right+1,bottom+1);
    }
    public static Bitmap Scale(Bitmap source, Rectangle src, int width, int height) {
        var output=new Bitmap(width,height,PixelFormat.Format32bppArgb);
        for(int y=0;y<height;y++) for(int x=0;x<width;x++) {
            int sx=src.X+Math.Min(src.Width-1,(int)((x+0.5)*src.Width/width));
            int sy=src.Y+Math.Min(src.Height-1,(int)((y+0.5)*src.Height/height));
            output.SetPixel(x,y,source.GetPixel(sx,sy));
        }
        return output;
    }
    public static void Icon(string input,string output,int width,int height,int logicalW,int logicalH,int spriteW,int spriteH) {
        using(var source=new Bitmap(input))
        using(var sprite=Scale(source,Bounds(source),spriteW,spriteH))
        using(var logical=new Bitmap(logicalW,logicalH,PixelFormat.Format32bppArgb)) {
            int dx=(logicalW-spriteW)/2,dy=(logicalH-spriteH)/2;
            for(int y=0;y<spriteH;y++) for(int x=0;x<spriteW;x++) logical.SetPixel(dx+x,dy+y,sprite.GetPixel(x,y));
            using(var final=Scale(logical,new Rectangle(0,0,logicalW,logicalH),width,height)) final.Save(output,ImageFormat.Png);
        }
    }
    public static void Hearts(string input,string sheetPath,string singlePath) {
        using(var source=new Bitmap(input))
        using(var sheet=new Bitmap(97,16,PixelFormat.Format32bppArgb)) {
            for(int i=0;i<5;i++) {
                int x0=source.Width*i/5,x1=source.Width*(i+1)/5;
                using(var cell=source.Clone(new Rectangle(x0,0,x1-x0,source.Height),PixelFormat.Format32bppArgb))
                using(var heart=Scale(cell,Bounds(cell),15,13)) {
                    for(int y=0;y<13;y++) for(int x=0;x<15;x++) sheet.SetPixel(i*19+2+x,1+y,heart.GetPixel(x,y));
                }
            }
            sheet.Save(sheetPath,ImageFormat.Png);
            using(var single=sheet.Clone(new Rectangle(0,0,19,16),PixelFormat.Format32bppArgb)) single.Save(singlePath,ImageFormat.Png);
        }
    }
    public static string Check(string path,int width,int height) {
        using(var b=new Bitmap(path)) {
            if(b.Width!=width || b.Height!=height) throw new Exception("Incorrect dimensions: "+path);
            int transparent=0,visible=0;
            for(int y=0;y<b.Height;y++) for(int x=0;x<b.Width;x++) {
                int a=b.GetPixel(x,y).A;
                if(a==0) transparent++;
                if(a>=128) visible++;
            }
            if(transparent==0 || visible==0) throw new Exception("Invalid transparency/content: "+path);
            return width+"x"+height+", alpha OK, visible="+visible;
        }
    }
}
'@
$specs = @(
    @('coin',1920,1920,32,32,28,28),
    @('raio',626,626,32,32,14,22),
    @('musculo',474,474,32,32,20,22),
    @('requa',474,474,32,32,26,6),
    @('nuvemChao',264,72,88,24,84,22)
)
$dest = Join-Path $root 'assets/Hud'
if (-not $VerifyOnly) {
    foreach($s in $specs) {
        [HudAssetPacking]::Icon((Join-Path $PSScriptRoot ('source/'+$s[0]+'.png')),(Join-Path $dest ($s[0]+'.png')),$s[1],$s[2],$s[3],$s[4],$s[5],$s[6])
    }
    [HudAssetPacking]::Hearts((Join-Path $PSScriptRoot 'source/health.png'),(Join-Path $dest 'coracao.png'),(Join-Path $dest 'coracao1.png'))
    $atlas = [Drawing.Bitmap]::new((Join-Path $PSScriptRoot 'source/atlas.png'))
    try {
        $packed = [HudAssetPacking]::Scale($atlas,[Drawing.Rectangle]::new(0,0,$atlas.Width,$atlas.Height),196,71)
        try { $packed.Save((Join-Path $dest 'coracoes.png'),[Drawing.Imaging.ImageFormat]::Png) } finally { $packed.Dispose() }
    } finally { $atlas.Dispose() }
    Get-ChildItem $dest -Filter '*.png' | Copy-Item -Destination (Join-Path $root 'assets/ui/hud')
}
foreach($s in ($specs + @(@('coracao',97,16),@('coracao1',19,16),@('coracoes',196,71)))) {
    $file = $s[0]+'.png'
    $path = Join-Path $dest $file
    $check = [HudAssetPacking]::Check($path,$s[1],$s[2])
    if((Get-FileHash $path).Hash -ne (Get-FileHash (Join-Path $root ('assets/ui/hud/'+$file))).Hash) { throw "HUD copy mismatch: $file" }
    Write-Output "$file $check; mirrored copy OK"
}

# Review the actual exported files on both light and dark game backgrounds.
$preview = [Drawing.Bitmap]::new(960,600)
$g = [Drawing.Graphics]::FromImage($preview)
$g.Clear([Drawing.ColorTranslator]::FromHtml('#111827'))
$font = [Drawing.Font]::new('Consolas',14)
$smallFont = [Drawing.Font]::new('Consolas',10)
$brush = [Drawing.SolidBrush]::new([Drawing.ColorTranslator]::FromHtml('#e5e7eb'))
$light = [Drawing.SolidBrush]::new([Drawing.ColorTranslator]::FromHtml('#c3b69e'))
try {
    $g.DrawString('PAWFIGHT / HUD', $font, $brush, 24, 18)
    $names = @('coin','raio','musculo','requa','nuvemChao')
    for($i=0;$i -lt $names.Count;$i++) {
        $x=24+$i*186
        $g.FillRectangle($light,$x,185,164,110)
        $g.DrawString($names[$i],$smallFont,$brush,$x,58)
        $src=[Drawing.Bitmap]::new((Join-Path $dest ($names[$i]+'.png')))
        try {
            $w=96; $h=96
            if($names[$i] -eq 'nuvemChao') { $w=150; $h=42 }
            $scaled=[HudAssetPacking]::Scale($src,[Drawing.Rectangle]::new(0,0,$src.Width,$src.Height),$w,$h)
            try {
                $g.DrawImageUnscaled($scaled,($x+(164-$w)/2),(85+(96-$h)/2))
                $g.DrawImageUnscaled($scaled,($x+(164-$w)/2),(192+(96-$h)/2))
            } finally { $scaled.Dispose() }
        } finally { $src.Dispose() }
    }
    $g.DrawString('VIDA / 100%  -  75%  -  50%  -  25%  -  0%', $smallFont,$brush,24,316)
    $src=[Drawing.Bitmap]::new((Join-Path $dest 'coracao.png'))
    try {
        $scaled=[HudAssetPacking]::Scale($src,[Drawing.Rectangle]::new(0,0,97,16),582,96)
        try { $g.DrawImageUnscaled($scaled,24,345) } finally { $scaled.Dispose() }
    } finally { $src.Dispose() }
    $g.DrawString('ATLAS AUXILIAR', $smallFont,$brush,24,462)
    $src=[Drawing.Bitmap]::new((Join-Path $dest 'coracoes.png'))
    try { $g.DrawImageUnscaled($src,24,490) } finally { $src.Dispose() }
    $preview.Save((Join-Path $PSScriptRoot 'preview.png'),[Drawing.Imaging.ImageFormat]::Png)
} finally {
    $g.Dispose(); $preview.Dispose(); $font.Dispose(); $smallFont.Dispose(); $brush.Dispose(); $light.Dispose()
}
