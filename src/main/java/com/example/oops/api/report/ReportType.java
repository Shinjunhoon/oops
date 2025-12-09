package com.example.oops.api.report;

public enum ReportType {

    ADVERTISEMENT("광고/도배"),

    /** 혐오 표현 및 차별적인 내용 */
    HATE_SPEECH("혐오/차별"),

    /** 불법 정보 (예: 도박, 판매 금지 물품 등) */
    ILLEGAL_CONTENT("불법 정보"),

    /** 음란성 및 선정적인 내용 */
    OBSCENITY("음란/선정성"),

    /** 사용자 비방, 욕설, 괴롭힘 */
    HARASSMENT("괴롭힘/욕설/비방"),

    /** 위 목록에 해당하지 않는 기타 사유 */
    OTHER("기타"),

    ADVERTISEMENTS("광고/도배"),


    SPAM_COMMENT("광고/도배성 댓글"),
    INSULT_HARASSMENT("욕설/비방/괴롭힘"),
    PORNOGRAPHY("음란/선정성"),
    PERSONAL_INFO_LEAK("개인정보 노출");



    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    /**
     * Enum의 한글 설명을 반환합니다. (프론트엔드 표시에 유용)
     * @return 신고 유형의 한글 설명
     */
    public String getDescription() {
        return description;
    }
}