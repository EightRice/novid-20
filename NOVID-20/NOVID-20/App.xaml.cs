using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using NOVID_20.Services;
using NOVID_20.Views;

namespace NOVID_20
{
    public partial class App : Application
    {

        public App()
        {
            InitializeComponent();

            DependencyService.Register<MockDataStore>();
            MainPage = new MainPage();
        }

        protected override void OnStart()
        {
        }

        protected override void OnSleep()
        {
        }

        protected override void OnResume()
        {
        }
    }
}
