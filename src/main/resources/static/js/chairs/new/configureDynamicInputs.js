import { $ } from "../../consts.js";

export function configureDynamicInputs() {
    $(document).ready(function() {
        $('#categories').select2({
            width: '100%',
            placeholder: "Selecione uma ou mais categorias",
            allowClear: true,
            ajax: {
                  url: '/admin/categories',
                  dataType:  'json',
                  processResults: function (data) {

                        return {
                            results: data.map(category => ({
                                id: category.id,
                                text: category.name
                            }))
                        };
                  }
            },
        language: {
                noResults: function (){

                    return `Categoria não encontrada`;
                }

            },

        });

        $("#pricing_group").select2({
            width: '100%',
            placeholder: "Selecione um grupo de precificação",
            allowClear: true,
            ajax: {
                url: '/admin/pricing-groups',
                dataType: 'json',
                processResults: function (data) {
                    return {
                        results: data.map(group => ({
                            id: group.id,
                            text: group.name
                        }))
                    };
                }
            },
        })
    })
}

