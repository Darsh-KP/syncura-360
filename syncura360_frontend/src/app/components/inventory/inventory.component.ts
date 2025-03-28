import { Component, OnInit, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { inventoryService, inventory, inventoryUpdateDto, inventoryUpdateRequest } from '../../services/inventory.service';
import { AgGridModule } from 'ag-grid-angular';
import { ColDef } from 'ag-grid-community';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { ItemFormComponent } from '../item-form/item-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AgGridModule,
    NavbarComponent,
    MatButtonModule,
  ],
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.css']
})
export class InventoryComponent implements OnInit {
  private dialog = inject(MatDialog);
  private inventorySvc = inject(inventoryService);

  inventoryList: inventory[] = [];
  gridApi: any;
  editedQuantities: { [ndc: number]: number } = {};

  errorMessage = '';
  successMessage = '';

  columnDefs: ColDef[] = [
    { headerName: 'NDC', field: 'ndc' },
    { headerName: 'Name', field: 'name' },
    {headerName: 'Pkg-Quantity', field: 'ppq' },
    { headerName: 'Quantity', field: 'quantity' },
    { headerName: 'Price', field: 'price' },
    {
      headerName: 'Actions',
      cellRenderer: (params: any) => {
        return `<button class="edit-btn">Edit</button>
 <button class="delete-btn" style="margin-left: 5px;">Delete</button>`;

      },
      onCellClicked: (params: any) => {
        const clickedElement = params.event.target as HTMLElement;
        if (clickedElement.classList.contains('edit-btn')) {
          this.openItemForm(params.data);
        } else if (clickedElement.classList.contains('delete-btn')) {
          this.deleteItem(params.data.ndc);
        }
      }
    }
  ];

  ngOnInit(): void {
    this.loadInventory();
  }

  loadInventory(): void {
    this.inventorySvc.getInventory().subscribe({
      next: data => {
        if (data && data.length > 0) {
          this.inventoryList = data;
          this.errorMessage = ''; // Clear any previous error
        } else {
          // Do nothing — or show a message if you want
          this.errorMessage = '';
        }
      },
      error: err => {
        this.errorMessage = 'Failed to load inventory.';
      }
    });
  }


  openItemForm(item?: inventory): void {
    const dialogRef = this.dialog.open(ItemFormComponent, {
      width: '500px',
      data: item || null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadInventory();
    });
  }

  onQuantityEdit(): void {
    const editedRows = this.gridApi.getRenderedNodes();
    this.editedQuantities = {};

    editedRows.forEach((row: any) => {
      const original = this.inventoryList.find(i => i.ndc === row.data.ndc);
      if (original && row.data.quantity !== original.quantity) {
        this.editedQuantities[row.data.ndc] = row.data.quantity;
      }
    });
  }

  deleteItem(ndc: number): void {
    if (confirm('Are you sure you want to delete this item?')) {
      this.inventorySvc.deleteInventory(ndc).subscribe({
        next: () => {
          this.successMessage = 'Item deleted successfully.';
          this.loadInventory();
        },
        error: () => {
          this.errorMessage = 'Failed to delete item.';
        }
      });
    }
  }


}
