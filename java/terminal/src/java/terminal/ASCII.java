package terminal;

public interface ASCII {

	public static final byte NUL = 0x00; // 000   0     00    NUL '\0'
	public static final byte SOH = 0x01; // 001   1     01    SOH (Start of Heading)
	public static final byte STX = 0x02; // 002   2     02    STX (Start of text)     
	public static final byte ETX = 0x03; // 003   3     03    ETX     
	public static final byte EOT = 0x04; // 004   4     04    EOT     
	public static final byte ENQ = 0x05; // 005   5     05    ENQ (Enquiry)   
	public static final byte ACK = 0x06; // 006   6     06    ACK     
	public static final byte BEL = 0x07; // 007   7     07    BEL '\a'
	public static final byte BS = 0x08; // 010   8     08    BS  '\b'
	public static final byte HT = 0x09; // 011   9     09    HT  '\t'
	public static final byte LF = 0x0A; // 012   10    0A    LF  '\n'
	public static final byte VT = 0x0B; // 013   11    0B    VT  '\v'
	public static final byte FF = 0x0C; // 014   12    0C    FF  '\f'
	public static final byte CR = 0x0D; // 015   13    0D    CR  '\r'
	public static final byte SO = 0x0E; // 016   14    0E    SO (Shift out -- code extension)      
	public static final byte SI = 0x0F; // 017   15    0F    SI (Shift in)      
	public static final byte DLE = 0x10; // 020   16    10    DLE (Data Link Escape)
	public static final byte DC1 = 0x11; // 021   17    11    DC1 (Device Control 1..4)  
	public static final byte DC2 = 0x12; // 022   18    12    DC2     
	public static final byte DC3 = 0x13; // 023   19    13    DC3     
	public static final byte DC4 = 0x14; // 024   20    14    DC4     
	public static final byte NAK = 0x15; // 025   21    15    NAK     
	public static final byte SYN = 0x16; // 026   22    16    SYN     
	public static final byte ETB = 0x17; // 027   23    17    ETB     
	public static final byte CAN = 0x18; // 030   24    18    CAN     
	public static final byte EM = 0x19; // 031   25    19    EM      
	public static final byte SUB = 0x1A; // 032   26    1A    SUB     
	public static final byte ESC = 0x1B; // 033   27    1B    ESC     
	public static final byte FS = 0x1C; // 034   28    1C    FS      
	public static final byte GS = 0x1D; // 035   29    1D    GS      
	public static final byte RS = 0x1E; // 036   30    1E    RS      
	public static final byte US = 0x1F; // 037   31    1F    US      
	public static final byte SPACE = 0x20; // 040   32    20    SPACE   
	public static final byte EXCLAMATION = 0x21; // 041   33    21    !       
	public static final byte DQUOTE = 0x22; // 042   34    22    "       
	public static final byte HASH = 0x23; // 043   35    23    #       
	public static final byte DOLLAR = 0x24; // 044   36    24    $       
	public static final byte PERCENT = 0x25; // 045   37    25    %       
	public static final byte AMPER = 0x26; // 046   38    26    &       
	public static final byte SQUOTE = 0x27; // 047   39    27    '       
	public static final byte LPAREN = 0x28; // 050   40    28    (       
	public static final byte RPAREN = 0x29; // 051   41    29    )       
	public static final byte ASTERISK = 0x2A; // 052   42    2A    *       
	public static final byte PLUS = 0x2B; // 053   43    2B    +       
	public static final byte COMMA = 0x2C; // 054   44    2C    ,       
	public static final byte MINUS = 0x2D; // 055   45    2D    -       
	public static final byte PERIOD = 0x2E; // 056   46    2E    .       
	public static final byte SLASH = 0x2F; // 057   47    2F    / 
	public static final byte ZERO = 0x30; // 060   48    30    0 
	public static final byte ONE = 0x31; // 061   49    31    1 
	public static final byte TWO = 0x32; // 062   50    32    2 
	public static final byte THREE = 0x33; // 063   51    33    3 
	public static final byte FOUR = 0x34; // 064   52    34    4 
	public static final byte FIVE = 0x35; // 065   53    35    5 
	public static final byte SIX = 0x36; // 066   54    36    6 
	public static final byte SEVEN = 0x37; // 067   55    37    7 
	public static final byte EIGHT = 0x38; // 070   56    38    8 
	public static final byte NINE = 0x39; // 071   57    39    9 
	public static final byte COLON = 0x3A; // 072   58    3A    : 
	public static final byte SEMICOLON = 0x3B; // 073   59    3B    ; 
	public static final byte LESS = 0x3C; // 074   60    3C    < 
	public static final byte EQUAL = 0x3D; // 075   61    3D    = 
	public static final byte GREATER = 0x3E; // 076   62    3E    > 
	public static final byte QUESTION = 0x3F; // 077   63    3F    ? 
	public static final byte AT = 0x40; // 100   64    40    @
	public static final byte A = 0x41; // 101   65    41    A
	public static final byte B = 0x42; // 102   66    42    B
	public static final byte C = 0x43; // 103   67    43    C
	public static final byte D = 0x44; // 104   68    44    D
	public static final byte E = 0x45; // 105   69    45    E
	public static final byte F = 0x46; // 106   70    46    F
	public static final byte G = 0x47; // 107   71    47    G
	public static final byte H = 0x48; // 110   72    48    H
	public static final byte I = 0x49; // 111   73    49    I
	public static final byte J = 0x4A; // 112   74    4A    J
	public static final byte K = 0x4B; // 113   75    4B    K
	public static final byte L = 0x4C; // 114   76    4C    L
	public static final byte M = 0x4D; // 115   77    4D    M
	public static final byte N = 0x4E; // 116   78    4E    N
	public static final byte O = 0x4F; // 117   79    4F    O
	public static final byte P = 0x50; // 120   80    50    P
	public static final byte Q = 0x51; // 121   81    51    Q
	public static final byte R = 0x52; // 122   82    52    R
	public static final byte S = 0x53; // 123   83    53    S
	public static final byte T = 0x54; // 124   84    54    T
	public static final byte U = 0x55; // 125   85    55    U
	public static final byte V = 0x56; // 126   86    56    V
	public static final byte W = 0x57; // 127   87    57    W
	public static final byte X = 0x58; // 130   88    58    X
	public static final byte Y = 0x59; // 131   89    59    Y
	public static final byte Z = 0x5A; // 132   90    5A    Z
	public static final byte LBRACK = 0x5B; // 133   91    5B    [
	public static final byte BACKSLASH = 0x5C; // 134   92    5C    \   '\\'
	public static final byte RBRACK = 0x5D; // 135   93    5D    ]
	public static final byte CARET = 0x5E; // 136   94    5E    ^
	public static final byte UNDERSCORE = 0x5F; // 137   95    5F    _
	public static final byte BACKTICK = 0x60; // 140   96    60    `
	public static final byte a = 0x61; // 141   97    61    a
	public static final byte b = 0x62; // 142   98    62    b
	public static final byte c = 0x63; // 143   99    63    c
	public static final byte d = 0x64; // 144   100   64    d
	public static final byte e = 0x65; // 145   101   65    e
	public static final byte f = 0x66; // 146   102   66    f
	public static final byte g = 0x67; // 147   103   67    g
	public static final byte h = 0x68; // 150   104   68    h
	public static final byte i = 0x69; // 151   105   69    i
	public static final byte j = 0x6A; // 152   106   6A    j
	public static final byte k = 0x6B; // 153   107   6B    k
	public static final byte l = 0x6C; // 154   108   6C    l
	public static final byte m = 0x6D; // 155   109   6D    m
	public static final byte n = 0x6E; // 156   110   6E    n
	public static final byte o = 0x6F; // 157   111   6F    o
	public static final byte p = 0x70; // 160   112   70    p
	public static final byte q = 0x71; // 161   113   71    q
	public static final byte r = 0x72; // 162   114   72    r
	public static final byte s = 0x73; // 163   115   73    s
	public static final byte t = 0x74; // 164   116   74    t
	public static final byte u = 0x75; // 165   117   75    u
	public static final byte v = 0x76; // 166   118   76    v
	public static final byte w = 0x77; // 167   119   77    w
	public static final byte x = 0x78; // 170   120   78    x
	public static final byte y = 0x79; // 171   121   79    y
	public static final byte z = 0x7A; // 172   122   7A    z
	public static final byte LCURLY = 0x7B; // 173   123   7B    {
	public static final byte PIPE = 0x7C; // 174   124   7C    |
	public static final byte RCURLY = 0x7D; // 175   125   7D    }
	public static final byte TILDE = 0x7E; // 176   126   7E    ~
	public static final byte DEL = 0x7F; // 177   127   7F    DEL

}
