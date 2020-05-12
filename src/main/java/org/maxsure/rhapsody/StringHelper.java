package org.maxsure.rhapsody;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class StringHelper {

    public String standardiseName(String name) {
        return name.replace("_", "");
    }

    public String splitCamelCase(String str) {
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(str), " ").trim()
                .replaceAll(" +", " ");
    }

}
