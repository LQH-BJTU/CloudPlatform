package com.sustar.ecsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 地域信息DTO - 用于前端展示的完整地域结构
 * 
 * 根据前端截图设计：
 * - 大区作为分组标题（如：亚太-中国、亚太-其他、欧洲与美洲）
 * - 每个大区下包含多个地域选项（如：华北1（青岛）、华北5（呼和浩特）等）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionAreaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 大区名称（用于展示分组标题）
     * 如：亚太-中国、亚太-其他、欧洲与美洲、中东
     */
    private String regionGroupName;

    /**
     * 大区编码（用于唯一标识）
     * 如：apac-cn、apac-other、emea-us、mea
     */
    private String regionGroupCode;

    /**
     * 下属地域列表
     */
    private List<AreaItemDTO> areas;

    /**
     * 地域项DTO - 单个地域选项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AreaItemDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 地域编码（唯一标识）
         * 如：cn-qingdao、cn-beijing、jp-tokyo
         */
        private String areaCode;

        /**
         * 地域展示名称（用于前端显示）
         * 如：华北1（青岛）、华北2（北京）、日本（东京）
         */
        private String displayName;

        /**
         * 是否选中（用于前端交互）
         */
        private Boolean selected;

        /**
         * 排序序号（用于控制显示顺序）
         */
        private Integer sortOrder;
    }
}
