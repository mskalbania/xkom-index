$(document).ready(function () {
    const ENDPOINT = "/api/v1/products/all";

    $("#input").keyup(function (event) {
            let searchQuery = event.target.value;
            if (searchQuery.length > 2) {
                console.log(searchQuery);
                $.getJSON(
                    ENDPOINT,
                    {
                        page: 1,
                        amount: 25,
                        like: searchQuery
                    },
                    function (data) {
                        console.log(data);
                        let list = $("#list");
                        list.empty();
                        $.each(data, function (idx, product) {
                                list.append(
                                    `<li class="list-group-item">
                                    Id: ${product.providerId}<br>   
                                    Name: ${product.name}<br>
                                    Url: <a href="${product.url}">${product.url}</a><br>
                                    Price change:<br>
                                    <canvas id="${idx}" width="400" height="50"></canvas>
                                    </li>`
                                )
                            }
                        );
                        renderCharts(data)
                    })
            }
        }
    );

    function renderCharts(data) {
        $.each(data, function (idx, product) {
            let ctx = $(`#${idx}`);
            let myLineChart = new Chart(ctx, {
                type: 'line',
                data: {
                    datasets: [{
                        label: 'Price change over days',
                        data: product.price.map(it => it.price),
                    }],
                    labels: product.price.map(it => it.timeStamp)
                },
                options: {
                    scales: {
                        yAxes: [{
                            stacked: true
                        }]
                    }
                }
            })
        });
    }
});