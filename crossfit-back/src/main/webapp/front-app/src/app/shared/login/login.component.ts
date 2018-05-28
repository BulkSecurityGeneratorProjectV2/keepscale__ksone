import { Component, OnInit, Input } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { Principal } from '../auth/principal.service';
import { AppService } from '../../app.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  private username: string;
  private password: string;
  private rememberme: boolean;

  private error;
  private loginFailed: boolean;
  private status: string;


  constructor(private authService: AuthService,  private router: Router, private principal: Principal, private appService:AppService) { }

  ngOnInit() {
    this.rememberme = true;
    this.loginFailed = false;
    this.appService.setTitle("Authentication");
  }

  onLogin() {
    this.status = "wait";
    this.loginFailed = false;

    this.authService.login(this.username, this.password, this.rememberme).subscribe(
      result=>{
        this.loginFailed = false;
        this.principal.identity(true).subscribe((account) => {
            // After the login the language will be changed to
            // the language selected by the user during his registration
            /*if (account !== null) {
                this.languageService.changeLanguage(account.langKey);
            }*/
          this.status = "success";
          this.router.navigate(['']);
        },
        err=>{
          this.status = "error";
          this.error = err.error;
        });

      },
      error=>{
        this.status = "error";
        this.error = new Object();
        this.error.message = error;
        this.loginFailed = true;
      }
    );
  }

}
