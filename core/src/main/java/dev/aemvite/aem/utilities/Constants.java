/*
 *  Copyright 2021 Chris Shaw
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package dev.aemvite.aem.utilities;

public class Constants {
    public static final String CLIENTLIB_TAG_JAVASCRIPT = "<script src=\"%s\"%s></script>\n";
    public static final String CLIENTLIB_TAG_STYLESHEET = "<link rel=\"stylesheet\" href=\"%s\"%s>\n";

    public static final String CLIENTLIB_BINDINGS_CATEGORIES = "categories";
    public static final String CLIENTLIB_BINDINGS_MODE = "mode";
    public static final String CLIENTLIB_BINDINGS_ESMODULE = "esModule";

    public static final String CLIENTLIB_MODULE_TYPE_ATTRIBUTE = " type=\"module\"";
    public static final String CLIENTLIB_NOMODULE_TYPE_ATTRIBUTE = " nomodule";

    public static final String CLIENTLIB_PROPERTY_ESMODULE = "esModule";
    public static final String CLIENTLIB_PROPERTY_NOMODULE = "noModule";

    private Constants() {
        // Does nothing as this class only contains static constants
    }
}
