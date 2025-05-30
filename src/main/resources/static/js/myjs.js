console.log("Script file executed");

const toggleSidebar = () => {
	if ($(".sidebar").is(":visible")) {
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	} else {
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
};

const search = () => {
	let query = $("#search-input").val();

	if (query == "") {
		$(".search-result").hide();
	} else {
		console.log(query);
		let url = `http://localhost:8282/search/${query}`;
		fetch(url)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				console.log(data);

				let text = `<div class='list=group'>`;

				data.forEach((contact) => {
					text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'> ${contact.name} </a>`;
				})

				text += `</div>`;

				$(".search-result").html(text);
				$(".search-result").show();
			})
	}
};

// first request - to server to create order

const paymentStart = () => {
	console.log("Payment started.......");
	var amount = $("#payment_field").val();
	console.log(amount);
	if (amount == "" || amount == null) {
		alert("Amount is required !!");
		return;
	}
	
	$.ajax(
		{
			url: '/user/create_order', 
			data: JSON.stringify({amount: amount, info: 'order_request'}),
			contentType: 'application/json',
			type: 'POST', 
			dataType: 'json',
			success: function(response) {
				// invoked when success
				console.log(response)
				if (response.status == "created") {
					// open payment form
					let options = {
						key: razorpayKey,
						amount: response.amount,
						currency: "INR",
						name: "Smart Contact Manager",
						description: "Donation",
						image: "https://www.gstatic.com/images/branding/product/2x/contacts_2022_48dp.png",
						order_id: response.id,
						handler: function(response) {
							console.log(response.razorpay_payment_id);
							console.log(response.razorpay_order_id);
							console.log(response.razorpay_signature);
							console.log("payment successful !!");
							
							updatePaymentOnServer(
								response.razorpay_payment_id,
								response.razorpay_order_id,
								"paid"
							);
						},
						prefill: {
							name: "",
							email: "",
							contact: "",
						},
						
						notes: {
							address: "LearnCodeWith Durgesh",
						},
						
						theme: {
							color: "#3399cc",
						}
					};
					
					let rzp = new Razorpay(options);
					
					rzp.on("payment.failed", function (response) {
						console.log(response.error.code);
						console.log(response.error.description);
						console.log(response.error.source);
						console.log(response.error.step);
						console.log(response.error.reason);
						console.log(response.error.metadata.order_id);
						console.log(response.error.metadata.payment_id);
						Swal.fire({
							icon: "error",
							title: "Your payment was not successful !!",
							showConfirmButton: true
						})
					});
					
					rzp.open();
				}
			},
			error: function(error) {
				// invoked when error
				console.log(error)
				alert("Something went wrong !!")
			}
	})
}

function updatePaymentOnServer(payment_id, order_id, status) {
	$.ajax({
		url: '/user/update_order', 
		data: JSON.stringify({
			payment_id: payment_id, 
			order_id: order_id,
			status: status,
		}),
		contentType: 'application/json',
		type: 'POST', 
		dataType: 'json',
		success: function (response) {
			//swal("Good job!", "Congrats !! Payment Successful !!", "Success")
			Swal.fire({
			  icon: "success",
			  title: "Your payment is success !!",
			  showConfirmButton: true,
			});
		},
		error: function (error) {
			Swal.fire({
				icon: "error",
				title: "Your payment is successful, but we did not get on server, we will contact you as soon as possible !!",
				showConfirmButton: true,
			});
			
		},
	});
}
