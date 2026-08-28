package cn.iocoder.yudao.module.mall.controller.app.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 商品分类 Response VO")
@Data
public class AppCategoryRespVO {

    @Schema(description = "分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "手机数码")
    private String name;

    @Schema(description = "排序值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sort;

}
