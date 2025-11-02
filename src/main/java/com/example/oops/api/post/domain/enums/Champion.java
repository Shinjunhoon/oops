package com.example.oops.api.post.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Champion {

    // 챔피언 목록 (영문 이름을 값으로 가짐)

    // A (아트록스)
    AATROX("Aatrox"),
    AHRI("Ahri"),
    AKALI("Akali"),
    AKSHAN("Akshan"),
    ALISTAR("Alistar"),
    AMUMU("Amumu"),
    ANIVIA("Anivia"),
    ANNIE("Annie"),
    APHELIOS("Aphelios"),
    ASHE("Ashe"),
    AURELION_SOL("Aurelion Sol"),
    AZIR("Azir"),

    // B
    BARD("Bard"),
    BELVETH("Bel'Veth"),
    BLITZCRANK("Blitzcrank"),
    BRAND("Brand"),
    BRAUM("Braum"),
    BRIAR("Briar"), // 최신 챔피언

    // C
    CAITLYN("Caitlyn"),
    CAMILLE("Camille"),
    CASSIOPEIA("Cassiopeia"),
    CHOGATH("Cho'Gath"),
    CORKI("Corki"),

    // D
    DARIUS("Darius"),
    DIANA("Diana"),
    DR_MUNDO("Dr. Mundo"),
    DRAVEN("Draven"),

    // E
    EKKO("Ekko"),
    ELISE("Elise"),
    EVELYNN("Evelynn"),
    EZREAL("Ezreal"),

    // F
    FIDDLESTICKS("Fiddlesticks"),
    FIORA("Fiora"),
    FIZZ("Fizz"),

    // G
    GALIO("Galio"),
    GANGPLANK("Gangplank"),
    GAREN("Garen"),
    GNAR("Gnar"),
    GRAGAS("Gragas"),
    GRAVES("Graves"),
    GWEN("Gwen"),

    // H
    HECARIM("Hecarim"),
    HEIMERDINGER("Heimerdinger"),

    // I
    ILLAOI("Illaoi"),
    IRELIA("Irelia"),
    IVERSON("Ivern"),

    // J
    JANNA("Janna"),
    JARVAN_IV("Jarvan IV"),
    JAX("Jax"),
    JAYCE("Jayce"),
    JHIN("Jhin"),
    JINX("Jinx"),

    // K
    KAISA("Kai'Sa"),
    KALISTA("Kalista"),
    KARMA("Karma"),
    KARTHUS("Karthus"),
    KASSADIN("Kassadin"),
    KATARINA("Katarina"),
    KAYLE("Kayle"),
    KAYN("Kayn"),
    KENNEN("Kennen"),
    KHAZIX("Kha'Zix"),
    KINDRED("Kindred"),
    KLED("Kled"),
    KOGMAW("Kog'Maw"),
    K_SANTE("K'Sante"), // 최신 챔피언

    // L
    LEBLANC("LeBlanc"),
    LEE_SIN("Lee Sin"),
    LEONA("Leona"),
    LILLIA("Lillia"),
    LISSANDRA("Lissandra"),
    LUCIAN("Lucian"),
    LULU("Lulu"),
    LUX("Lux"),

    // M
    MALPHITE("Malphite"),
    MALZAHAR("Malzahar"),
    MAOKAI("Maokai"),
    MASTER_YI("Master Yi"),
    MILIO("Milio"), // 최신 챔피언
    MISS_FORTUNE("Miss Fortune"),
    MORDEKAISER("Mordekaiser"),
    MORGANA("Morgana"),

    // N
    NAAFIRI("Naafiri"), // 최신 챔피언
    NAMI("Nami"),
    NASUS("Nasus"),
    NAUTILUS("Nautilus"),
    NEEKO("Neeko"),
    NIDALEE("Nidalee"),
    NILAH("Nilah"),
    NOCTURNE("Nocturne"),
    NUNU_WILLUMP("Nunu & Willump"),

    // O
    OLAF("Olaf"),
    ORIANNA("Orianna"),
    ORNN("Ornn"),

    // P
    PANTHEON("Pantheon"),
    POPULAR("Poppy"),
    PYKE("Pyke"),

    // Q
    QUINN("Quinn"),

    // R
    RAKAN("Rakan"),
    RAMMUS("Rammus"),
    REKSAI("Rek'Sai"),
    RELL("Rell"),
    RENATA_GLASC("Renata Glasc"),
    RENEKTON("Renekton"),
    RENGAR("Rengar"),
    RIVEN("Riven"),
    RUMBLE("Rumble"),
    RYZE("Ryze"),

    // S
    SAMIRA("Samira"),
    SEJUANI("Sejuani"),
    SENNA("Senna"),
    SERAPHINE("Seraphine"),
    SETT("Sett"),
    SHACO("Shaco"),
    SHEN("Shen"),
    SHYVANA("Shyvana"),
    SINGED("Singed"),
    SION("Sion"),
    SIVIR("Sivir"),
    SKARNER("Skarner"),
    SONA("Sona"),
    SORAKA("Soraka"),
    SWAIN("Swain"),
    SYLAS("Sylas"),
    SYNDRA("Syndra"),

    // T
    TAHM_KENCH("Tahm Kench"),
    TALIYAH("Taliyah"),
    TALON("Talon"),
    TARIC("Taric"),
    TEEMO("Teemo"),
    THRESH("Thresh"),
    TRISTANA("Tristana"),
    TRUNDLE("Trundle"),
    TRYNDAMERE("Tryndamere"),
    TWISTED_FATE("Twisted Fate"),
    TWITCH("Twitch"),

    // U
    UDYR("Udyr"),
    URGOT("Urgot"),

    // V
    VARUS("Varus"),
    VAYNE("Vayne"),
    VEIGAR("Veigar"),
    VELKOZ("Vel'Koz"),
    VEX("Vex"),
    VI("Vi"),
    VIEGO("Viego"),
    VIKTOR("Viktor"),
    VLADIMIR("Vladimir"),
    VOLIBEAR("Volibear"),

    // W
    WARWICK("Warwick"),
    WUKONG("Wukong"),

    // X
    XAYAH("Xayah"),
    XERATH("Xerath"),
    XIN_ZHAO("Xin Zhao"),

    // Y
    YASUO("Yasuo"),
    YONE("Yone"),
    YORICK("Yorick"),
    YUUMI("Yuumi"),

    // Z
    ZAC("Zac"),
    ZED("Zed"),
    ZERI("Zeri"),
    ZIGGS("Ziggs"),
    ZILEAN("Zilean"),
    ZOE("Zoe"),
    ZYRA("Zyra");


    // 챔피언의 실제 영문 이름을 저장할 필드
    private final String englishName;
}