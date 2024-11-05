import {NgModule} from '@angular/core';

import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatSliderModule} from '@angular/material/slider';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatBadgeModule} from '@angular/material/badge';
import {MatBottomSheetModule} from '@angular/material/bottom-sheet';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';

import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatExpansionModule} from '@angular/material/expansion';

import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressBarModule} from '@angular/material/progress-bar';

import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatRadioModule} from '@angular/material/radio';
import {MatSelectModule} from '@angular/material/select';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatSortModule} from '@angular/material/sort';
import {MatStepperModule} from '@angular/material/stepper';
import {MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatTreeModule} from '@angular/material/tree';
import { MatNativeDateModule } from '@angular/material/core';

@NgModule({
    imports:[
        MatButtonModule,MatCardModule,MatIconModule,MatMenuModule,
        MatSidenavModule,MatToolbarModule,MatGridListModule,MatSliderModule,
        MatAutocompleteModule,MatBadgeModule,MatBottomSheetModule,MatButtonToggleModule,
        MatCheckboxModule,MatChipsModule,
        MatDatepickerModule,MatDialogModule,MatDividerModule,MatExpansionModule,
        MatInputModule,MatListModule,MatPaginatorModule,MatProgressBarModule,
        MatProgressSpinnerModule,MatRadioModule,MatSelectModule,MatSlideToggleModule,
        MatSnackBarModule,MatSortModule,MatStepperModule,MatTableModule,MatNativeDateModule,
        MatTabsModule,MatTooltipModule,MatTreeModule
    ],
    exports:[
        MatButtonModule,MatCardModule,MatIconModule,MatMenuModule,
        MatSidenavModule,MatToolbarModule,MatGridListModule,MatSliderModule,
        MatAutocompleteModule,MatBadgeModule,MatBottomSheetModule,MatButtonToggleModule,
        MatCheckboxModule,MatChipsModule,
        MatDatepickerModule,MatDialogModule,MatDividerModule,MatExpansionModule,
        MatInputModule,MatListModule,MatPaginatorModule,MatProgressBarModule,
        MatProgressSpinnerModule,MatRadioModule,MatSelectModule,MatSlideToggleModule,
        MatSnackBarModule,MatSortModule,MatStepperModule,MatTableModule,MatNativeDateModule,
        MatTabsModule,MatTooltipModule,MatTreeModule
        
    ]
})
export class MaterialModule{}