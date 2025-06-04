import { getOrderReportsByChair } from './getOrderReportsByChair.js';
import { generateOptionsForChair, generateOptionsForCategory } from './buildOptions.js';
import { getOrderReportsByCategory } from './getOrderReportsByCategory.js';

const ApexCharts = window.ApexCharts;
const startDateByChairInput = document.getElementById('date1-chairs');
const endDateByChairInput = document.getElementById('date2-chairs');
const startDateByCategoryInput = document.getElementById('date1-categories');
const endDateByCategoryInput = document.getElementById('date2-categories');
const chartChair = document.querySelector("#chart1");
const chartCategory = document.querySelector("#chart2");

var previousStartDateByChair = startDateByChairInput.value;
var previousEndDateByChair = endDateByChairInput.value;
var previousStartDateByCategory = startDateByCategoryInput.value;
var previousEndDateByCategory = endDateByCategoryInput.value;



function generateSalesDataForCategories() {

    const endDate = new Date();
    const startDate = new Date(endDate);
    startDate.setMonth(startDate.getMonth() - 6);

    const categories = [
        { name: "Escritório", color: "#FF1654" },
        { name: "Gamer", color: "#247BA0" },
        { name: "Executiva", color: "#4CAF50" },
        { name: "Ergonômica", color: "#FFC107" }
    ];

    const data = categories.map(category => {
        const data = [];
        let currentDate = new Date(startDate);

        while (currentDate <= endDate) {
            data.push({
                x: new Date(currentDate),
                y: Math.floor(Math.random() * 100) + 50
            });

            currentDate.setMonth(currentDate.getMonth() + 1);
        }


        return {
        name: category.name,
        data: data,
        color: category.color
        };
    });



    return data;
}

//For categories
const options2 = {
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
    colors: generateSalesDataForCategories().map(category => category.color),
    series: generateSalesDataForCategories(),
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
}

let chartChairInstance = null;
let chartCategoryInstance = null;
document.addEventListener('DOMContentLoaded', async function() {
    const chairReport = await getOrderReportsByChair(previousStartDateByChair, previousEndDateByChair);
    const categoryReport = await getOrderReportsByCategory(previousStartDateByCategory, previousEndDateByCategory);
    if (chairReport.status !== 200 || categoryReport.status !== 200) {
        return;
    }
    const optionsForChair = generateOptionsForChair(await chairReport.json());
    const optionsForCategory = generateOptionsForCategory(await categoryReport.json());

    chartChairInstance = new ApexCharts(chartChair, optionsForChair);
    chartCategoryInstance = new ApexCharts(chartCategory, optionsForCategory);

    chartChairInstance.render();
    chartCategoryInstance.render();
});

async function fetchReportsByChair(startDate, endDate) {
    const response = await getOrderReportsByChair(startDate, endDate);
    if (response.status !== 200) {
        return;
    }
    const options = generateOptionsForChair(await response.json());
    chartChairInstance.updateOptions({
        series: options.series,
        xaxis: {
            ...options.xaxis,
            min: new Date(startDate).getTime(),
            max: new Date(endDate).getTime()
        }
    })
}

async function fetchReportsByCategory(startDate, endDate) {
    const response = await getOrderReportsByCategory(startDate, endDate);
    if (response.status !== 200) {
        return;
    }
    const options = generateOptionsForCategory(await response.json());
    chartCategoryInstance.updateOptions({
        series: options.series,
        xaxis: {
            ...options.xaxis,
            min: new Date(startDate).getTime(),
            max: new Date(endDate).getTime()
        }
    })
}
configureEndDateInput(endDateByChairInput, startDateByChairInput, fetchReportsByChair, previousEndDateByChair);
configureStartDateInput(startDateByChairInput, endDateByChairInput, fetchReportsByChair, previousStartDateByChair);
configureEndDateInput(endDateByCategoryInput, startDateByCategoryInput, fetchReportsByCategory, previousEndDateByCategory);
configureStartDateInput(startDateByCategoryInput, endDateByCategoryInput, fetchReportsByCategory, previousStartDateByCategory);

function configureEndDateInput(input, startInput, functionToFetchReports, previousEnd) {
   input.addEventListener('input', async () => {
        if (input.value === previousEnd) {
            return;
        }

        if (input.value < startInput.value) {
            input.value = previousEnd;
            return;
        }

        if (new Date(input.value) > new Date()) {
            input.value = previousEnd;
            return;
        }

        previousEnd = input.value;

        await functionToFetchReports(startInput.value, previousEnd);
   });
}

function configureStartDateInput(input, endInput, functionToFetchReports, previousStart) {
    input.addEventListener('input', async () => {
        if (input.value === previousStart) {
            return;
        }

        if (input.value > endInput.value) {
            input.value = previousStart;
            return;
        }

        previousStart = input.value;

        await functionToFetchReports(previousStart, endInput.value);
    });
}

