/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module('app', ['ui.bootstrap', 'ngMessages','remoteValidation','ui.router','ngRoute'])
        .directive('dynamic', function ($compile) {
            return {
                restrict: 'A',
                replace: true,
                link: function (scope, ele, attrs) {
                    scope.$watch(attrs.dynamic, function (html) {
                        ele.html(html);
                        $compile(ele.contents())(scope);
                    });
                }
            };
        })
        .directive('fileModel', ['$parse', function ($parse) {
                return {
                    restrict: 'A',
                    link: function (scope, element, attrs) {
                        var model = $parse(attrs.fileModel);
                        var modelSetter = model.assign;
                        element.bind('change', function () {
                            scope.$apply(function () {
                                modelSetter(scope, element[0].files[0]);
                            });
                        });
                    }
                };
            }]);

app.config(function ($httpProvider) {
    $httpProvider.defaults.headers.common['X-protect'] = 'true';
});
//This configures the routes and associates each route with a view and a controller
app.config(["$routeProvider", function ($routeProvider, $http) {
	           // console.log("routeProvider!!!!" + routeProvider);
        $routeProvider
                .when('/review',
                        {
                            controller: 'appController',
                            templateUrl: 'app/partials/review.html'
                        })
                .when('/upload',
                        {
                            controller: 'appController',
                            templateUrl: 'app/partials/upload.html'
                        })
                .otherwise({redirectTo: '/upload'});
    }]);
app.run([
    '$rootScope',
    function ($rootScope) {
        $rootScope.$on('$routeChangeStart', function (event, next, current) {
				            console.log("routeLoading!!!!");
            $rootScope.isRouteLoading = true;
        });
        $rootScope.$on('$routeChangeSuccess', function (event, next, current) {
                       console.log("route success!!!!");
		   $rootScope.isRouteLoading = false;
        });
    }
]);
app.service('appService', function ($http, $rootScope) {
	
    this.upload = function (file) {
		var filename = file.name;
		var fdata = new FormData();
        fdata.append("file", file);
       
		
		var promise = $http.post('/fileprocessor/path/endpoint/upload/' + filename, fdata, {
                  transformRequest: angular.identity,
                  headers: {'Content-Type': undefined}
               })
            
               .success(function(data, status, headers, config){
				   console.log('responsecode ' + data.responseCode);
			console.log('time of upload ' + data.uploadTime);
            if (data.responseCode !== '0') {
                error("Message", data.responseMessage);
            } else {
				success("Message", data.responseMessage); 
			}
               })
            
               .error(function(data, status, headers, config){
				      error('Error', status);
               });

        return promise;
    };
	
	    this.review = function (fileName,reviewType) {
		var reviewMethod = 'reviewValid'
		if(reviewType==='invalid') {
			reviewMethod = 'reviewInvalid';
		}
		console.log('reviewMethod:: ' + reviewMethod);
        var promise = $http({
            method: 'GET',
            url: '/fileprocessor/path/endpoint/' + reviewMethod + '/' + fileName
        }).success(function (data, status, headers, config) {
			return data;
        }).error(function (data, status, headers, config) {
            error('Error', status);
        });

        return promise;
    };
    
});

app.controller('appController', function ($scope, $http, $rootScope, $modal, $log, appService) {

    init();
    function init() {
        console.log("Angular JS Started!!!!");
		  $rootScope.fileName = "";
        $scope.data = {};

    };

	
    $scope.processUpload = function () {
        console.log('Method called!!!');
        $rootScope.isRouteLoading = true;
        console.log('File details here is ' + $scope.myFile.name);
        if ($scope.myFile === undefined || $scope.myFile === '') {
            $rootScope.isRouteLoading = false;
            warning("Message", "Kindly select file to upload.");
        } else {
            appService.upload($scope.myFile).then(
                    function (data) {
						$scope.myFile= '';
						$rootScope.isRouteLoading = false;
             });
			
        }

    };
	
	
	   $scope.review = function () {
        console.log('Method2 called!!!');
        $rootScope.isRouteLoading = true;
        console.log('File name here is ' + $scope.fileName + '   ' + $scope.reviewType);
        if ($scope.fileName === undefined || $scope.fileName === '') {
            $rootScope.isRouteLoading = false;
            warning("Message", "Kindly provide a file name.");
        } else {
			 console.log('loading!!!' + $rootScope.isRouteLoading);
            appService.review($scope.fileName,$scope.reviewType).then(
                    function (response) {
						$scope.data = response.data;
						$rootScope.isRouteLoading = false;
                    });
			
        }

    };

});
function warning(header, message) {
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "positionClass": "toast-center",
        "onclick": null,
        "showDuration": "1000",
        "hideDuration": "1000",
        "timeOut": "5000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
    toastr.warning(message, header);
}
function success(header, message) {
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "positionClass": "toast-center",
        "onclick": null,
        "showDuration": "1000",
        "hideDuration": "1000",
        "timeOut": "10000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
    toastr.success(message, header);

}
function error(header, message) {
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "positionClass": "toast-center",
        "onclick": null,
        "showDuration": "1000",
        "hideDuration": "1000",
        "timeOut": "10000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
    toastr.error(message, header);
}



