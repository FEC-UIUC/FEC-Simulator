
var stockchart;
    
function makeChart(start_bars, start_vol) {	
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
            text : 'Live Data'
        },
                
        yAxis: [{
                labels: {
                    align: 'right',
                    x: -3
                },
                title: {
                    text: 'OHLC'
                },
                height: '60%',
                lineWidth: 2
            }, {
                labels: {
                    align: 'right',
                    x: -3
                },
                title: {
                    text: 'Volume'
                },
                top: '65%',
                height: '35%',
                offset: 0,
                lineWidth: 2
        }],
    
        xAxis : {
            max: 100,
            min: 100
        },
            
        series : [{
            name : 'OHLC',
            type : 'candlestick',
            data : start_bars,
            yAxis: 0
        },{
            type: 'column',
            name: 'Volume',
            data: start_vol,
            yAxis: 1
        }]
    }).highcharts();
	
} 