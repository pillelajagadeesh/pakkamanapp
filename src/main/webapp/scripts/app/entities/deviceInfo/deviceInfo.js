'use strict';

angular.module('reachoutApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('deviceInfo', {
                parent: 'entity',
                url: '/deviceInfos',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.deviceInfo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/deviceInfo/deviceInfos.html',
                        controller: 'DeviceInfoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('deviceInfo');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('deviceInfo.detail', {
                parent: 'entity',
                url: '/deviceInfo/{id}',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.deviceInfo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/deviceInfo/deviceInfo-detail.html',
                        controller: 'DeviceInfoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('deviceInfo');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'DeviceInfo', function($stateParams, DeviceInfo) {
                        return DeviceInfo.get({id : $stateParams.id});
                    }]
                }
            })
            .state('deviceInfo.new', {
                parent: 'deviceInfo',
                url: '/new',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/deviceInfo/deviceInfo-dialog.html',
                        controller: 'DeviceInfoDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    device: null,
                                    sdk: null,
                                    model: null,
                                    product: null,
                                    consumerId: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('deviceInfo', null, { reload: true });
                    }, function() {
                        $state.go('deviceInfo');
                    })
                }]
            })
            .state('deviceInfo.edit', {
                parent: 'deviceInfo',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/deviceInfo/deviceInfo-dialog.html',
                        controller: 'DeviceInfoDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['DeviceInfo', function(DeviceInfo) {
                                return DeviceInfo.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('deviceInfo', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('deviceInfo.delete', {
                parent: 'deviceInfo',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/deviceInfo/deviceInfo-delete-dialog.html',
                        controller: 'DeviceInfoDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['DeviceInfo', function(DeviceInfo) {
                                return DeviceInfo.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('deviceInfo', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
