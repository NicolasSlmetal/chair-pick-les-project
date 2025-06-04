

export function generateOptionsForChair(data) {
    const uniqueChairs = [...new Set(data.map(chair => chair.chairName))];

    const series = uniqueChairs.map(chairName => {
        return {
            name: chairName,
            data: data
                .filter(chair => chair.chairName === chairName)
                .map(item => ({
                    x: new Date(item.date),
                    y: item.soldValue
                }))
        };
    });
    return {
     chart: {
       height: 400,
       type: "line",
       stacked: false,
       zoom: {
         enabled: true
       },
       toolbar: {
         show: true
       },
       fontFamily: 'Roboto, sans-serif'
     },
     dataLabels: {
       enabled: false
     },
     series: series,
     stroke: {
       width: 3,
       curve: 'smooth'
     },
     grid: {
       borderColor: '#e7e7e7',
       row: {
         colors: ['#f3f3f3', 'transparent'],
         opacity: 0.5
       }
     },
     markers: {
       size: 6
     },
     xaxis: {
       type: "datetime",
       title: {
         text: "Data"
       },
       labels: {
         format: 'dd/MM/yy',
         datetimeUTC: false
       }
     },
     yaxis: {
       title: {
         text: "Vendas (R$)"
       },
       labels: {
         formatter: function(value) {
           return "R$ " + value.toLocaleString('pt-BR');
         }
       }
     },
     tooltip: {
       shared: true,
       y: {
         formatter: function(value) {
           return "R$ " + value.toLocaleString('pt-BR');
         }
       }
     },
     legend: {
       position: 'top',
       horizontalAlign: 'center',
       floating: false,
       offsetY: -25,
       offsetX: -5
     },
     title: {
       text: 'Vendas por Produto ao Longo do Tempo',
       align: 'center',
       style: {
         fontSize: '18px'
       }
     },
     subtitle: {
       text: 'Valores em Reais (R$)',
       align: 'center'
     }
   };

}

export function generateOptionsForCategory(data) {
    const uniqueChairs = [...new Set(data.map(category => category.categoryName))];

        const series = uniqueChairs.map(categoryName => {
            return {
                name: categoryName,
                data: data
                    .filter(category => category.categoryName === categoryName)
                    .map(item => ({
                        x: new Date(item.date),
                        y: item.soldValue
                    }))
            };
        });
        return {
         chart: {
           height: 400,
           type: "line",
           stacked: false,
           zoom: {
             enabled: true
           },
           toolbar: {
             show: true
           },
           fontFamily: 'Roboto, sans-serif'
         },
         dataLabels: {
           enabled: false
         },
         series: series,
         stroke: {
           width: 3,
           curve: 'smooth'
         },
         grid: {
           borderColor: '#e7e7e7',
           row: {
             colors: ['#f3f3f3', 'transparent'],
             opacity: 0.5
           }
         },
         markers: {
           size: 6
         },
         xaxis: {
           type: "datetime",
           title: {
             text: "Data"
           },
           labels: {
             format: 'dd/MM/yy',
             datetimeUTC: false
           }
         },
         yaxis: {
           title: {
             text: "Vendas (R$)"
           },
           labels: {
             formatter: function(value) {
               return "R$ " + value.toLocaleString('pt-BR');
             }
           }
         },
         tooltip: {
           shared: true,
           y: {
             formatter: function(value) {
               return "R$ " + value.toLocaleString('pt-BR');
             }
           }
         },
         legend: {
           position: 'top',
           horizontalAlign: 'center',
           floating: false,
           offsetY: -25,
           offsetX: -5
         },
         title: {
           text: 'Vendas por Categoria ao Longo do Tempo',
           align: 'center',
           style: {
             fontSize: '18px'
           }
         },
         subtitle: {
           text: 'Valores em Reais (R$)',
           align: 'center'
         }
       };
}