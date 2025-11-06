package com.example.oops.api.post.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Champion {

    // 챔피언 목록 (영문 이름과 한글 이름을 값으로 가짐)

    // A
    AATROX("Aatrox", "아트록스"),
    AHRI("Ahri", "아리"),
    AKALI("Akali", "아칼리"),
    AKSHAN("Akshan", "아크샨"),
    ALISTAR("Alistar", "알리스타"),
    AMUMU("Amumu", "아무무"),
    ANIVIA("Anivia", "애니비아"),
    ANNIE("Annie", "애니"),
    APHELIOS("Aphelios", "아펠리오스"),
    ASHE("Ashe", "애쉬"),
    AURELION_SOL("Aurelion Sol", "아우렐리온 솔"),
    AZIR("Azir", "아지르"),

    // B
    BARD("Bard", "바드"),
    BELVETH("Bel'Veth", "벨베스"),
    BLITZCRANK("Blitzcrank", "블리츠크랭크"),
    BRAND("Brand", "브랜드"),
    BRAUM("Braum", "브라움"),
    BRIAR("Briar", "브라이어"), // 최신 챔피언

    // C
    CAITLYN("Caitlyn", "케이틀린"),
    CAMILLE("Camille", "카밀"),
    CASSIOPEIA("Cassiopeia", "카시오페아"),
    CHOGATH("Cho'Gath", "초가스"),
    CORKI("Corki", "코르키"),

    // D
    DARIUS("Darius", "다리우스"),
    DIANA("Diana", "다이애나"),
    DR_MUNDO("Dr. Mundo", "문도 박사"),
    DRAVEN("Draven", "드레이븐"),

    // E
    EKKO("Ekko", "에코"),
    ELISE("Elise", "엘리스"),
    EVELYNN("Evelynn", "이블린"),
    EZREAL("Ezreal", "이즈리얼"),

    // F
    FIDDLESTICKS("Fiddlesticks", "피들스틱"),
    FIORA("Fiora", "피오라"),
    FIZZ("Fizz", "피즈"),

    // G
    GALIO("Galio", "갈리오"),
    GANGPLANK("Gangplank", "갱플랭크"),
    GAREN("Garen", "가렌"),
    GNAR("Gnar", "나르"),
    GRAGAS("Gragas", "그라가스"),
    GRAVES("Graves", "그레이브즈"),
    GWEN("Gwen", "그웬"),

    // H
    HECARIM("Hecarim", "헤카림"),
    HEIMERDINGER("Heimerdinger", "하이머딩거"),

    // I
    ILLAOI("Illaoi", "일라오이"),
    IRELIA("Irelia", "이렐리아"),
    IVERSON("Ivern", "아이번"),

    // J
    JANNA("Janna", "잔나"),
    JARVAN_IV("Jarvan IV", "자르반 4세"),
    JAX("Jax", "잭스"),
    JAYCE("Jayce", "제이스"),
    JHIN("Jhin", "진"),
    JINX("Jinx", "징크스"),

    // K
    KAISA("Kai'Sa", "카이사"),
    KALISTA("Kalista", "칼리스타"),
    KARMA("Karma", "카르마"),
    KARTHUS("Karthus", "카서스"),
    KASSADIN("Kassadin", "카사딘"),
    KATARINA("Katarina", "카타리나"),
    KAYLE("Kayle", "케일"),
    KAYN("Kayn", "케인"),
    KENNEN("Kennen", "케넨"),
    KHAZIX("Kha'Zix", "카직스"),
    KINDRED("Kindred", "킨드레드"),
    KLED("Kled", "클레드"),
    KOGMAW("Kog'Maw", "코그모"),
    K_SANTE("K'Sante", "크산테"), // 최신 챔피언

    // L
    LEBLANC("LeBlanc", "르블랑"),
    LEE_SIN("Lee Sin", "리 신"),
    LEONA("Leona", "레오나"),
    LILLIA("Lillia", "릴리아"),
    LISSANDRA("Lissandra", "리산드라"),
    LUCIAN("Lucian", "루시안"),
    LULU("Lulu", "룰루"),
    LUX("Lux", "럭스"),

    // M
    MALPHITE("Malphite", "말파이트"),
    MALZAHAR("Malzahar", "말자하"),
    MAOKAI("Maokai", "마오카이"),
    MASTER_YI("Master Yi", "마스터 이"),
    MILIO("Milio", "밀리오"), // 최신 챔피언
    MISS_FORTUNE("Miss Fortune", "미스 포츈"),
    MORDEKAISER("Mordekaiser", "모데카이저"),
    MORGANA("Morgana", "모르가나"),

    // N
    NAAFIRI("Naafiri", "나피리"), // 최신 챔피언
    NAMI("Nami", "나미"),
    NASUS("Nasus", "나서스"),
    NAUTILUS("Nautilus", "노틸러스"),
    NEEKO("Neeko", "니코"),
    NIDALEE("Nidalee", "니달리"),
    NILAH("Nilah", "닐라"),
    NOCTURNE("Nocturne", "녹턴"),
    NUNU_WILLUMP("Nunu & Willump", "누누와 윌럼프"),

    // O
    OLAF("Olaf", "올라프"),
    ORIANNA("Orianna", "오리아나"),
    ORNN("Ornn", "오른"),

    // P
    PANTHEON("Pantheon", "판테온"),
    POPULAR("Poppy", "뽀삐"),
    PYKE("Pyke", "파이크"),

    // Q
    QUINN("Quinn", "퀸"),

    // R
    RAKAN("Rakan", "라칸"),
    RAMMUS("Rammus", "람머스"),
    REKSAI("Rek'Sai", "렉사이"),
    RELL("Rell", "렐"),
    RENATA_GLASC("Renata Glasc", "레나타 글라스크"),
    RENEKTON("Renekton", "레넥톤"),
    RENGAR("Rengar", "렝가"),
    RIVEN("Riven", "리븐"),
    RUMBLE("Rumble", "럼블"),
    RYZE("Ryze", "라이즈"),

    // S
    SAMIRA("Samira", "사미라"),
    SEJUANI("Sejuani", "세주아니"),
    SENNA("Senna", "세나"),
    SERAPHINE("Seraphine", "세라핀"),
    SETT("Sett", "세트"),
    SHACO("Shaco", "샤코"),
    SHEN("Shen", "쉔"),
    SHYVANA("Shyvana", "쉬바나"),
    SINGED("Singed", "신지드"),
    SION("Sion", "사이온"),
    SIVIR("Sivir", "시비르"),
    SKARNER("Skarner", "스카너"),
    SONA("Sona", "소나"),
    SORAKA("Soraka", "소라카"),
    SWAIN("Swain", "스웨인"),
    SYLAS("Sylas", "사일러스"),
    SYNDRA("Syndra", "신드라"),

    // T
    TAHM_KENCH("Tahm Kench", "탐 켄치"),
    TALIYAH("Taliyah", "탈리야"),
    TALON("Talon", "탈론"),
    TARIC("Taric", "타릭"),
    TEEMO("Teemo", "티모"),
    THRESH("Thresh", "쓰레쉬"),
    TRISTANA("Tristana", "트리스타나"),
    TRUNDLE("Trundle", "트런들"),
    TRYNDAMERE("Tryndamere", "트린다미어"),
    TWISTED_FATE("Twisted Fate", "트위스티드 페이트"),
    TWITCH("Twitch", "트위치"),

    // U
    UDYR("Udyr", "우디르"),
    URGOT("Urgot", "우르곳"),

    // V
    VARUS("Varus", "바루스"),
    VAYNE("Vayne", "베인"),
    VEIGAR("Veigar", "베이가"),
    VELKOZ("Vel'Koz", "벨코즈"),
    VEX("Vex", "벡스"),
    VI("Vi", "바이"),
    VIEGO("Viego", "비에고"),
    VIKTOR("Viktor", "빅토르"),
    VLADIMIR("Vladimir", "블라디미르"),
    VOLIBEAR("Volibear", "볼리베어"),

    // W
    WARWICK("Warwick", "워윅"),
    WUKONG("Wukong", "오공"),

    // X
    XAYAH("Xayah", "자야"),
    XERATH("Xerath", "제라스"),
    XIN_ZHAO("Xin Zhao", "신 짜오"),

    // Y
    YASUO("Yasuo", "야스오"),
    YONE("Yone", "요네"),
    YORICK("Yorick", "요릭"),
    YUUMI("Yuumi", "유미"),

    // Z
    ZAC("Zac", "자크"),
    ZED("Zed", "제드"),
    ZERI("Zeri", "제리"),
    ZIGGS("Ziggs", "직스"),
    ZILEAN("Zilean", "질리언"),
    ZOE("Zoe", "조이"),
    ZYRA("Zyra", "자이라");


    // 챔피언의 실제 영문 이름을 저장할 필드
    private final String englishName;
    // 챔피언의 한글 이름을 저장할 필드 추가
    private final String koreanName;
}