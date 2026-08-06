package com.wordbattle.game.logic

import java.util.*

object WordDictionary {
    
    private val wordSet = HashSet<String>()
    
    init {
        loadDictionary()
    }
    
    private fun loadDictionary() {
        // Common English words for the game
        val words = listOf(
            // 2-letter words
            "AA", "AB", "AD", "AE", "AG", "AH", "AI", "AL", "AM", "AN", "AR", "AS", "AT", "AW", "AX", "AY",
            "BA", "BE", "BI", "BO", "BY",
            "CH", "DA", "DE", "DI", "DO", "DU", "DY",
            "EA", "ED", "EF", "EH", "EL", "EM", "EN", "ER", "ES", "ET", "EX",
            "FA", "FE",
            "GI", "GO",
            "HA", "HE", "HI", "HM", "HO",
            "ID", "IF", "IN", "IS", "IT",
            "JO",
            "KA", "KI",
            "LA", "LI", "LO",
            "MA", "ME", "MI", "MM", "MO", "MU", "MY",
            "NA", "NE", "NO",
            "OA", "OD", "OE", "OF", "OH", "OI", "OM", "ON", "OP", "OR", "OS", "OU", "OW", "OX", "OY",
            "PA", "PE", "PI", "QI",
            "RE",
            "SH", "SI", "SO",
            "TA", "TE", "TI", "TO",
            "UH", "UM", "UN", "UP", "US", "UT",
            "WE", "WO",
            "XI", "XU",
            "YA", "YE", "YO",
            "ZA",
            
            // 3-letter words
            "ACE", "ACT", "ADD", "AGE", "AGO", "AID", "AIM", "AIR", "ALL", "AND", "ANT", "ANY", "APE", "ARC", "ARE", "ARK", "ARM", "ART", "ASH", "ASK", "ATE", "AUG", "AUK", "AVA", "AVE", "AWE", "AXE", "AZE",
            "BAD", "BAG", "BAN", "BAR", "BAT", "BAY", "BED", "BEE", "BIG", "BIN", "BIT", "BOW", "BOX", "BOY", "BUD", "BUG", "BUN", "BUS", "BUT", "BUY",
            "CAB", "CAM", "CAP", "CAR", "CAT", "CAW", "COW", "CRY", "CUB", "CUP", "CUT",
            "DAB", "DAM", "DAY", "DEN", "DEW", "DID", "DIG", "DIM", "DIP", "DO", "DOG", "DAM", "DOT", "DRY", "DUB", "DU", "DUD", "DUE", "DUG", "DUN",
            "EAR", "EAT", "EAU", "EBB", "ECO", "EDH", "EE", "EFT", "EGG", "EGO", "ELD", "ELF", "ELM", "END", "ENS", "ERA", "ERR", "ESS", "EST", "ETC", "EVE", "EW", "EWE", "EYE",
            "FAB", "FAD", "FAG", "FAN", "FAT", "FED", "FEH", "FEM", "FEN", "FEU", "FEW", "FIB", "FIG", "FIN", "FIR", "FIT", "FIX", "FLU", "FLY", "FOE", "FOG", "FOR", "FOX", "FOW", "FRE", "FRY", "FUB", "FUN", "FUR",
            "GAB", "GAD", "GAG", "GAL", "GAM", "GAP", "GAS", "GAT", "GAY", "GED", "GEE", "GEK", "GEM", "GET", "GIG", "GIL", "GIN", "GIT", "GNU", "GO", "GOD", "GOL", "GON", "GOT", "GOX", "GOW", "GOW", "GUB", "GUE", "GUN", "GUT", "GUY",
            "HAD", "HAH", "HAJ", "HAM", "HAO", "HAP", "HAS", "HAT", "HAW", "HAY", "HE", "HEM", "HEN", "HER", "HEW", "HEX", "HIB", "HID", "HI", "HIM", "HIP", "HIS", "HIT", "HMM", "HOB", "HOD", "HOE", "HOG", "HOH", "HOI", "HOM", "HOP", "HOT", "HOW", "HUB", "HUE", "HUG", "HUH", "HUM", "HUT",
            "ICE", "ICY", "ID", "ILL", "IMP", "INK", "INN", "ION", "IRE", "IVY",
            "JAB", "JAG", "JAM", "JAR", "JAW", "JAY", " JEEB", "JEE", "JEH", "JEO", "JEW", "JIG", "JIN", "JOB", "JOE", "JOG", "JOT", "JOY", "JUG", "JUM",
            "KAB", "CAD", "CAM", "CAN", "CAP", "CAR", "CAT", "CAW", "CAX", "CAY", "CEE", "CEL", "CEP", "CHI", "CHO", "CIM", "CIN", "CIP", "CIS", "CIT", "COG", "COL", "CON", "COO", "COP", "COT", "COW", "COX", "COY", "COZ", "CRU", "CRW", "CRY", "CUD", "CUE", "CUM", "CUP", "CUR", "CUS", "CUT", "CYN",
            "DAB", "DAC", "DAD", "DAE", "DAG", "DAH", "DAL", "DAM", "DAN", "DAO", "DAP", "DAR", "DAS", "DAT", "DAW", "DAX", "DAY", "DEB", "DEE", "DEL", "DEM", "DEN", "DEO", "DEP", "DER", "DES", "DEW", "DEX", "DEY", "DIA", "DIB", "DIC", "DID", "DIE", "DIF", "DIG", "DIM", "DIN", "DIP", "DIR", "DIS", "DIT", "DIV", "DOB", "DOC", "DOE", "DOG", "DON", "DOO", "DOP", "DOT", "DOW", "DOX", "DOY", "DOZ", "DRY", "DUD", "DUE", "DUG", "DUH", "DUI", "DUN", "DUO", "DUP", "DUS", "DUT", "DUX", "DWA", "DYN",
            "EBB", "ECO", "EDT", "EEL", "EGG", "EGO", "ELT", "EMU", "END", "ERE", "ERR", "EST", "ETA", "ETC", "ETT", "EVE", "EW", "EWE", "EYE",
            "FAB", "FAD", "FAG", "FAH", "FAN", "FAR", "FAS", "FAT", "FAX", "FAY", "FED", "FEE", "FEH", "FEM", "FEN", "FEU", "FEW", "FEY", "FIB", "FID", "FIE", "FIG", "FIL", "FIN", "FIR", "FIT", "FIX", "FIZ", "FLO", "FLU", "FLY", "FOE", "FOG", "FON", "FOO", "FOP", "FOR", "FOX", "FOY", "FRE", "FRY", "FUB", "FUD", "FUE", "FUG", "FUH", "FUI", "FUN", "FUR", "FUS", "FUT", "FYE",
            "GAB", "GAD", "GAE", "GAG", "GAL", "GAM", "GAN", "GAP", "GAR", "GAS", "GAT", "GAU", "GAV", "GAW", "GAY", "GEE", "GEL", "GEM", "GEN", "GET", "GHE", "GHI", "GHY", "GIG", "GIL", "GIN", "GIP", "GIT", "GJE", "GLO", "GNA", "GNU", "GO", "GOA", "GOB", "GOD", "GOE", "GOG", "GOO", "GOP", "GOT", "GOX", "GOY", "GUD", "GUE", "GUF", "GUG", "GUH", "GUI", "GUK", "GUP", "GUR", "GUS", "GUT", "GUU", "GUY", "GYM", "GYP",
            "HAE", "HAG", "HAH", "HAJ", "HAM", "HAO", "HAP", "HAS", "HAT", "HAU", "HAW", "HAY", "HE", "HEF", "HEM", "HEN", "HEO", "HER", "HES", "HET", "HEW", "HEY", "HI", "HIB", "HID", "HIE", "HIM", "HIN", "HIP", "HIS", "HIT", "HIV", "HMM", "HOA", "HOB", "HOC", "HOD", "HOE", "HOG", "HOH", "HOI", "HOM", "HON", "HOO", "HOP", "HOS", "HOT", "HOW", "HOY", "HUB", "HUC", "HUD", "HUE", "HUG", "HUH", "HUI", "HUK", "HUL", "HUM", "HUN", "HUP", "HUT", "HYE", "HYM", "HYP",
            "IAC", "ICE", "ICK", "ICS", "ID", "IDE", "IDS", "IFF", "IFT", "IGL", "IGM", "IGN", "IGO", "IHS", "ICK", "IDS", "IFF", "IFT", "IKE", "ILL", "ILY", "IMP", "INK", "INN", "ION", "IRE", "IRK", "ISM", "IST", "ITE", "IVY",
            "JAB", "JAD", "JAG", "JAM", "JAR", "JAW", "JAY", "JAZZ", "JEE", "JEH", "JEO", "JEW", "JIG", "JIN", "JIS", "JOB", "JOE", "JOG", "JOT", "JOY", "JUG", "JUM", "JUT",
            "KAB", "KAE", "KAI", "KAS", "KAT", "KAY", "KEB", "KED", "KEG", "KEN", "KEP", "KER", "KES", "KET", "KEX", "KEY", "KHI", "KIA", "KID", "KIF", "KIN", "KIP", "KIS", "KIT", "KIW", "KOA", "KOB", "KOF", "KON", "KOP", "KOS", "KOT", "KOW", "KOY", "KUF", "KUG", "KUH", "KUI", "KUK", "KUL", "KUM", "KUN", "KUP", "KUR", "KUS", "KUT", "KUZ", "KYD", "KYE", "KYI", "KYO", "KYU",
            "LAB", "LAC", "LAD", "LAG", "LAH", "LAI", "LAM", "LAP", "LAR", "LAS", "LAT", "LAW", "LAX", "LAY", "LEA", "LED", "LEE", "LEG", "LEH", "LEI", "LEK", "LEO", "LER", "LES", "LET", "LEU", "LEV", "LEW", "LEX", "LEY", "LIB", "LID", "LIE", "LIG", "LIM", "LIN", "LIP", "LIQ", "LIS", "LIT", "LIU", "LIV", "LIZ", "LOB", "LOC", "LOD", "LOE", "LOG", "LOH", "LOI", "LOL", "LOM", "LON", "LOO", "LOP", "LOS", "LOT", "LOU", "LOV", "LOX", "LOY", "LUA", "LUC", "LUD", "LUE", "LUG", "LUH", "LUK", "LUM", "LUN", "LUP", "LUS", "LUT", "LUX", "LUZ", "LYD", "LYE", "LYM", "LYN", "LYO", "LYS", "LYT",
            "MAD", "MAE", "MAG", "MAN", "MAO", "MAP", "MAR", "MAS", "MAT", "MAU", "MAW", "MAX", "MAY", "MED", "MEE", "MEL", "MEM", "MEN", "MET", "MEW", "MEZ", "MIC", "MID", "MIE", "MIG", "MIL", "MIM", "MIN", "MIO", "MIR", "MIS", "MIT", "MIX", "MIZ", "MOA", "MOB", "MOD", "MOE", "MOG", "MOH", "MOI", "MOM", "MON", "MOO", "MOP", "MOR", "MOS", "MOT", "MOW", "MOX", "MOY", "MOZ", "MUD", "MUE", "MUG", "MUH", "MUI", "MUK", "MUL", "MUM", "MUN", "MUP", "MUR", "MUS", "MUT", "MUZ", "MYC", "MYL", "MYN", "MYR", "MYS", "MYT",
            "NAB", "NAE", "NAG", "NAH", "NAI", "NAM", "NAP", "NAY", "NE", "NEB", "NED", "NEE", "NEF", "NEG", "NEH", "NEI", "NEK", "NEL", "NEM", "NEN", "NEO", "NEP", "NER", "NES", "NET", "NEU", "NEW", "NEX", "NEY", "NIC", "NID", "NIE", "NIL", "NIM", "NIP", "NIS", "NIT", "NIX", "NO", "NOD", "NOE", "NOG", "NOH", "NOI", "NOL", "NON", "NOO", "NOP", "NOR", "NOS", "NOT", "NOU", "NOV", "NOW", "NOX", "NOY", "NOZ", "NU", "NUB", "NUC", "NUD", "NUE", "NUG", "NUH", "NUI", "NUK", "NUL", "NUM", "NUN", "NUP", "NUR", "NUS", "NUT", "NYL", "NYS", "NYT",
            "OAF", "OAK", "OAR", "OAT", "OBA", "OBE", "OBI", "OBO", "OCA", "ODA", "ODS", "OED", "OES", "OFF", "OFT", "OHS", "OIA", "OIC", "OID", "OIL", "OIN", "ION", "IOT", "IOS", "IRE", "IRK", "ISM", "IST", "ITE", "ITS", "IVE", "OWE", "owl", "OWN", "OXO", "OYS",
            "PAD", "PAH", "PAL", "PAM", "PAN", "PAO", "PAP", "PAR", "PAT", "PAU", "PAW", "PAX", "PAY", "PEA", "PEC", "PEG", "PEH", "PEI", "PEK", "PEL", "PEN", "PEP", "PER", "PES", "PET", "PEW", "PEX", "PEY", "PHI", "PHT", "PIC", "PID", "PIE", "PIG", "PIL", "PIN", "PIO", "PIS", "PIT", "PIU", "PIX", "PIY", "PIZ", "PLY", "PO", "POD", "POE", "POG", "POH", "POI", "POL", "POM", "PON", "POO", "POP", "POR", "POS", "POT", "POW", "POX", "POY", "POZ", "PRA", "PRO", "PRS", "PRY", "PSY", "PUB", "PUE", "PUG", "PUH", "PUP", "PUR", "PUS", "PUT", "PUZ", "PYA", "PYE", "PYX",
            "QAT", "QIS", "QUA", "QUB", "QUC", "QUE", "QUF", "QUG", "QUH", "QUI", "QUK", "QUL", "QUM", "QUN", "QUP", "QUR", "QUS", "QUU", "QUY", "QUZ",
            "RAD", "RAG", "RAH", "RAI", "RAM", "RAN", "RAO", "RAP", "RAS", "RAT", "RAW", "RAY", "RE", "REA", "RED", "REE", "REF", "REG", "REH", "REI", "REK", "REM", "REN", "REO", "REP", "RES", "RET", "REU", "REV", "REW", "REX", "REY", "RHE", "RHO", "RIA", "RIB", "RID", "RIF", "RIG", "RIH", "RIK", "RIL", "RIM", "RIN", "RIO", "RIP", "RIS", "RIT", "RIX", "RIZ", "ROB", "ROC", "ROD", "ROE", "ROG", "ROH", "ROI", "ROM", "RON", "ROO", "ROP", "ROR", "ROS", "ROT", "ROW", "ROX", "ROY", "RUB", "RUE", "RUG", "RUH", "RUI", "RUK", "RUM", "RUN", "RUP", "RUR", "RUS", "RUT", "RUZ", "RYE", "RYS", "RYT",
            "SAB", "SAC", "SAD", "SAE", "SAG", "SAH", "SAI", "SAL", "SAM", "SAN", "SAO", "SAP", "SAR", "SAT", "SAW", "SAX", "SAY", "SEA", "SEC", "SED", "SEE", "SEG", "SEH", "SEI", "SEK", "SEL", "SEN", "SEO", "SEP", "SER", "SES", "SET", "SEU", "SEV", "SEW", "SEX", "SEY", "SHA", "SHY", "SIB", "SIC", "SID", "SIE", "SIG", "SIH", "SIK", "SIL", "SIM", "SIN", "SIO", "SIP", "SIR", "SIS", "SIX", "SIY", "SIZ", "SKA", "SKE", "SKI", "SKO", "SKU", "SLY", "SMA", "SNE", "SNO", "SNU", "SO", "SOD", "SOE", "SOG", "SOH", "SOI", "SOL", "SOM", "SON", "SOO", "SOP", "SOS", "SOT", "SOW", "SOX", "SOY", "SOZ", "SPA", "SPY", "SRI", "STY", "SUB", "SUE", "SUM", "SUN", "SUP", "SUR", "SUS", "SWY",
            "TAB", "TAC", "TAD", "TAE", "TAG", "TAH", "TAI", "TAJ", "TAM", "TAN", "TAO", "TAP", "TAR", "TAS", "TAT", "TAW", "TAX", "TAY", "TEA", "TED", "TEE", "TEF", "TEG", "TEH", "TEI", "TEK", "TEL", "TEN", "TEO", "TEP", "TER", "TES", "TET", "TEU", "TEW", "TEX", "CEY", "THE", "THO", "THU", "THY", "TIA", "TIC", "TIE", "TIG", "TIL", "TIN", "TIO", "TIP", "TIR", "TIS", "TIT", "TIX", "TIY", "TIZ", "TOE", "TOG", "TOH", "TOI", "TOM", "TON", "TOO", "TOP", "TOS", "TOT", "TOW", "TOX", "TOY", "TOZ", "TRG", "TSK", "TUB", "TUG", "TUH", "TUI", "TUK", "TUM", "TUN", "TUP", "TUR", "TUS", "TUT", "TUZ", "TWA", "TWO", "TYE", "TYM", "TYN", "TYS", "TYT",
            "Udo", "UGH", "UKE", "ULU", "UM", "UN", "UP", "UR", "US", "UT", "UVA", "UYS",
            "VAC", "VAE", "VAN", "VAP", "VAT", "VAW", "VAH", "VAK", "VAL", "VAM", "VANE", "VAR", "VAS", "VEH", "VEL", "VEN", "VET", "VEU", "VEY", "VIA", "VIC", "VID", "VIE", "VIG", "VIL", "VIN", "VIO", "VIPS", "VIR", "VIS", "VIT", "VIX", "VIZ", "VOA", "VOE", "VOG", "VOH", "VOI", "VOL", "VOM", "VON", "VOO", "VOP", "VOR", "VOS", "VOT", "VOW", "VOX", "VOY", "VOZ", "VUG", "VUM", "VYA", "VYE", "VYU",
            "WAE", "WAG", "WAH", "WAI", "WAK", "WAL", "WAM", "WAN", "WAO", "WAP", "WAR", "WAS", "WAT", "WAW", "WAX", "WAY", "WEB", "WED", "WEE", "WEH", "WEK", "WEL", "WEM", "WEN", "WEO", "WEP", "WER", "WES", "WEST", "WET", "WEU", "WEY", "WHO", "WHY", "WIB", "WIC", "WID", "WIE", "WIG", "WIM", "WIN", "WIT", "WIY", "WIZ", "WOA", "WOB", "WOE", "WOG", "WOH", "WOI", "WOK", "WOL", "WON", "WOO", "WOP", "WOS", "WOT", "WOW", "WOX", "COY", "WOZ", "WRE", "WRY", "WUD", "WUE", "WUG", "WUH", "WUI", "WUK", "WUM", "WUN", "WUP", "WUR", "WUS", "WUT", "WY",
            "XIS", "XMAS", "XRAY", "XUX", "XYLYL", "XYST",
            "YAO", "YAP", "YAW", "YAY", "YEA", "YEK", "YEL", "YEN", "YEO", "YEP", "YER", "YES", "YET", "YEW", "YEX", "YE", "YID", "YIK", "YIN", "YIP", "YOB", "YOD", "YOE", "YOK", "YOM", "YON", "YOP", "YOS", "YOT", "YOU", "YOW", "YOX", "YOY", "YUA", "YUE", "YUG", "YUH", "YUI", "YUK", "YUM", "YUN", "YUP", "YUR", "YUS", "YUT", "YUZ", "YVE", "YWA", "YY",
            "ZAG", "ZAH", "ZAI", "ZAN", "ZAP", "ZAR", "ZAX", "ZAY", "ZEA", "ZED", "ZEE", "ZEK", "ZEN", "ZEP", "ZER", "ZES", "ZET", "ZEW", "ZEZ", "ZHI", "ZIG", "ZIN", "ZIP", "ZIR", "ZIS", "ZIT", "ZIZ", "ZOA", "ZOO", "ZOS", "ZUZ", "ZZI"
        )
        
        wordSet.addAll(words.map { it.uppercase() })
    }
    
    fun isValidWord(word: String): Boolean {
        return wordSet.contains(word.uppercase())
    }
    
    fun isPrefix(prefix: String): Boolean {
        return wordSet.any { it.startsWith(prefix.uppercase()) }
    }
}
