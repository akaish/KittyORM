package net.akaish.kitty.orm.annotations.table.index;

import net.akaish.kitty.orm.enums.AscDesc;

public @interface INDEX_ENTRY {
    String columnName();
    AscDesc sortingOrder() default AscDesc.NOT_SET_SKIP_OR_DEFAULT; // TODO add to INDEX instead columns
}