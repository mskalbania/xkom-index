$(document).ready(function () {
    const ENDPOINT = "/api/v1/products/all"

    $("#input").keyup(function (event) {
            let searchQuery = event.target.value;
            if (searchQuery.length > 2) {
                console.log(searchQuery);
                $.getJSON(
                    ENDPOINT,
                    {
                        page: 1,
                        amount: 50, //INTRODUCE PAGINATION
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
                                    ${table(product.price)}
                                    </li>`
                                )
                            }
                        )
                    })
            }
        }
    );

    function table(price) {
        return `<table class="table"> 
                  <thead>
                    <tr>
                      <th scope="col">TimeStamp</th>
                      <th scope="col">Price</th>
                    </tr>
                  </thead>
                  <tbody>
                    ${price.map(p => `<tr>
                                                <td>${p.timeStamp}</td>
                                                <td>${p.price}</td>
                                               </tr>`)
                           .join("")
                     }
                  </tbody>
                </table>`
    }
});