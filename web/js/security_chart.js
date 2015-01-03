<<<<<<< HEAD

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
            text : 'Live Data'
        },
                
        yAxis: [{
                labels: {
                    align: 'right',
                    x: -3
                },
                title: {
                    text: 'Bid/Ask Spread'
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
            
        series : [{
            name : 'Live data',
            type : 'line',
            data : []
        },{
            type: 'column',
            name: 'Volume',
            data: [],
            yAxis: 1
        }]
    }).highcharts();
	
=======

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
            data : [[1,100,100,100,100]],
            yAxis: 0
        },{
            type: 'column',
            name: 'Volume',
            data: [],
            yAxis: 1
        }]
    }).highcharts();
	
>>>>>>> 57dbd26655d3f50701c0bf4ce51fda57f917261d
} 