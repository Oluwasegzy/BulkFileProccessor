/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
angular.module('Select2', [])
        .directive('select2', function() {
            'use strict';

            return {
                restrict: 'A',
                priority: -10,
                require: 'ngModel',
                link: function(scope, element, attr, ngModel) {
                    element.select2({
                        allowClear: true
                    });

                    if (attr.select2Sync) {
                        scope.$watch(function( ) {
                            return ngModel.$viewValue;
                        }, function(value) {
//                            console.log('select values = ' + value);
                            element.select2("val", value);
                        });
                    }
                }
            };
        }).directive('clickDrop', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.click(function() {
                element.closest('.col-md-12').find('.' + attrs.clickDrop).slideToggle();
            });
        }
    };
})
        .directive('loadHtml', function() {
            return {
                restrict: 'A',
                priority: -10,
//                terminal: false,
//                template: function(tElm, tAttr) {
//                    console.log(tAttr);
//                    console.log(tAttr.loadHtml);
//                    return '<p>well chosen</p>';
//                },
//                templateUrl: '/somewhere',
                replace: true,
//                scope:String or boolean,
//                transclude: true,
//                controller: function(scope, tElm, tAttr, transclude, otherinjectibles) {
//
//                },
//                controllerAS: 'my controller',
//                require: 'required',
                link: function(scope, element, attrs) {
//                    $('.dropper').click('<p>i have been chosen</p>');
//                    console.log(attrs);
//                    console.log(attrs.loadHtml);
                }
//                compile: function(element, attrs, transclude) {
//                    return {
//                        pre: function(scope, tElm, tAttr, controller) {
//                        },
//                        post: function(scope, tElm, tAttr, controller) {
//                        }
//                    };
////                    return function(scope, tElm, tAttr, controller){}
//                }
            };
        });
