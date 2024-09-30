/**
 * Copyright © 2011-2024 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * toggle: used for the image dropdown.
 *
 */
jQuery(document).ready(function () {
    $.ajax({
        url: 'https://nginx.mousephenotype-dev.org/data/api/v1/genes/' + gene_id + '/gene_external_links',
        type: "GET",
        success: function (results) {
            $('.container#external-links').removeClass('hidden');
            results.forEach(function (r) {
                var linkDiv = $("<div class='mb-3 col-12'></div>");
                var linkWithProvider = "<div class='link-wrapper'><b>"+ r.providerName +"</b> - <a class='primary link' href='" + r.href  + "' target='_blank'>" + r.label + "</a><i class='fal fa-external-link fa-xs'></i></div>";
                linkDiv.append(linkWithProvider);
                var list = $("<ul></ul>");
                list.append("<li>" + !!r.description ? r.description : "" + "</li>");
                linkDiv.append(list);
                $('#external-links-content').append(linkDiv);
            });
        }
    });
});