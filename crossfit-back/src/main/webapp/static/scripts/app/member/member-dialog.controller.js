'use strict';

angular.module('crossfitApp').controller('MemberDialogController',
    ['$q', '$scope', '$stateParams', '$state', '$uibModalInstance', 'Member', 'Membership', 'Booking', 'Authority', 'Bill',
        function($q, $scope, $stateParams, $state, $modalInstance, Member, Membership, Booking, Authority, Bill) {

    	$scope.now = new Date();

        $scope.member = {};
        $scope.memberBookings = [];
        $scope.view = $stateParams.view;

        if (!$stateParams.id){
            $scope.member = {
                title: 'MR', langKey: 'fr',
                telephonNumber: null, sickNoteEndDate: null,
                membershipStartDate: null, membershipEndDate: null,
                level: null, id: null,
                roles : ["ROLE_USER"],
                subscriptions : [
                    {
                        subscriptionStartDate : new Date()
                    }
                ]
            };
        }

        $scope.memberships = Membership.query();
        $scope.roles = Authority.query();
        $scope.paymentMethods = Bill.paymentMethods();
        
        $scope.$on('$locationChangeSuccess', function(event) { 
        	$scope.view = $stateParams.view;
        });

        
        $scope.save = function (andQuit) {
        	var callBack = function(result){
                $scope.$emit('crossfitApp:memberUpdate', result);
            	if (andQuit){
                    $modalInstance.close(result);
            	}
            	else{
            	    if (!$stateParams.id){
                        $state.go('member.edit', {id: result.id});
            	    }
            	    else{
                        $scope.loadMember();
                        $scope.loadBooking();
            	    }
            	}
            };
            
            if ($scope.member.id != null) {
                Member.update($scope.member, callBack);
            } else {
                Member.save($scope.member, callBack);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.loadMember = function(){
            if ($stateParams.id != null) {
                $scope.member = null;
                Member.get({id : $stateParams.id}, function(result){
                    $scope.member = result;
                });
            }
        }
        $scope.loadBooking = function(){
            if ($stateParams.id != null) {
                $scope.memberBookings = null;
	        	Booking.getByMember({memberId : $stateParams.id}, function(resultBookings) {
	            	$scope.memberBookings = resultBookings;
	            });
            }
        }

        $scope.openedSubscription = [];
        $scope.openedMandats = [];

        $scope.isOpen = function(arr, obj){
        	return arr.indexOf(obj) > -1;
        }

        
        $scope.toggle = function(event, arr, obj){

        	
        	var idx = arr.indexOf(obj);

			// Is currently selected
			if (idx > -1) {
				arr.splice(idx, 1);
			}

			// Is newly selected
			else {
				arr.push(obj);
			}
        }

        $scope.addSubscription = function() {
        	//TODO: Recupere les membership par defaut
        	$scope.member.subscriptions.push({
        		subscriptionStartDate : new Date(),
        		bookingCount: 0
        	});
        };


        $scope.addMandat = function() {
        	$scope.member.mandates.push({
        		rum : 'toto',
        		ics: 'ABS'
        	});
        };

        $scope.deleteSubscription = function(subscription) {
        	var idx = $scope.member.subscriptions.indexOf(subscription);
        	$scope.member.subscriptions.splice(idx, 1);
        };
        
        $scope.calculateEndDate = function(subscription){
        	if (!subscription.subscriptionEndDate){
        		$scope.addMonthToSubscriptionEndDate(subscription);
        	}
        }
        $scope.caculateCssDate = function(actual, prev, next, dir){

        	if (actual != null && actual.subscriptionEndDate != null){
	        	if (actual.subscriptionEndDate.getTime() < actual.subscriptionStartDate.getTime()){
	        		return "has-error";
	        	}
        	}
        	
        	var compareTo = dir === 'asc' ? prev : next;
        	
        	if (compareTo != null && compareTo.subscriptionEndDate != null && 
    				compareTo.subscriptionEndDate.getTime() > actual.subscriptionStartDate.getTime()){
    			return "has-error";
    		}
        }
        
        $scope.addMonthToSubscriptionEndDate = function(subscription){
        	if(!subscription.membership){
        		return;
        	}
        	
        	var d = new Date(subscription.subscriptionEndDate ? subscription.subscriptionEndDate : subscription.subscriptionStartDate);
        	
        	d.setMonth(d.getMonth() +  subscription.membership.nbMonthValidity);
        	
        	subscription.subscriptionEndDate = d;
        }
        
        $scope.isSubscriptionInvalid = function(s){
        	var invalid = !s.membership || 
        		!s.subscriptionEndDate || !s.subscriptionStartDate &&
        		!s.paymentMethod;
        	
        	if (!invalid && $scope.mustFillDirectDebitInfo(s)){
        		invalid = !s.directDebitAfterDate ||
	        		!s.directDebitAtDayOfMonth ||
	        		!s.directDebitFirstPaymentTaxIncl ||
	        		!s.directDebitFirstPaymentMethod ||
	        		!s.directDebitIban ||
	        		!s.directDebitBic;
        	}
        	
        	return invalid;
        }
        
        $scope.mustFillDirectDebitInfo = function(s){
    		var must =	s.paymentMethod=='DIRECT_DEBIT' && 
    			(
	        		s.directDebitAfterDate ||
	        		s.directDebitAtDayOfMonth ||
	        		s.directDebitFirstPaymentTaxIncl ||
	        		s.directDebitFirstPaymentMethod ||
	        		s.directDebitIban ||
	        		s.directDebitBic 
    		);
    		return must;
        }
        
        $scope.showView = function(viewname){
        	$scope.view = viewname;
        }
        

        $scope.quickDeleteBooking = function(booking){
        	if (confirm("Supprimer la réservation de "+booking.title+" ?")){
            	Booking.delete({id : booking.id}, function(){
            		$scope.loadBooking();
            	});
        	}
        }
        
        
        $scope.toggleSelectedRole = function toggleSelectedRole(role) {
			var idx = $scope.member.roles.indexOf(role);

			// Is currently selected
			if (idx > -1) {
				$scope.member.roles.splice(idx, 1);
			}

			// Is newly selected
			else {
				$scope.member.roles.push(role);
			}
		};


        $scope.loadMember();
        $scope.loadBooking();

}]);
