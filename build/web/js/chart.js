
var stockchart;
    
function makeChart() {	
	Highcharts.setOptions({
        global : {
            useUTC : false
        }
    });

    // Create the chart
    stockchart = $('#chart-area').highcharts('StockChart', {

        rangeSelector: {
            buttons: [{
                count: 1,
                type: 'minute',
                text: '1M'
            }, {
                count: 5,
                type: 'minute',
                text: '5M'
            }, {
                type: 'all',
                text: 'All'
            }],
            inputEnabled: false,
            selected: 0
        },

        title : {
            text : 'Live random data'
        },

        exporting: {
            enabled: false
        },

         series : [{
            name : 'Live data',
            data : (function () {
                // generate an array of random data
                var data = [], time = (new Date()).getTime(), i;

                for (i = -999; i <= 0; i += 1) {
                    data.push([
                        time + i * 1000,
                        Math.random()
                    ]);
                }
                return data;
            }())
        }]
    }).highcharts();
	
} 