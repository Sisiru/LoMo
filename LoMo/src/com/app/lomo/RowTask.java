package com.app.lomo;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RowTask extends ArrayAdapter<String> {
	private Context context;
	private List<String> myArray;
	private List<String> priority;
	private List<String> idArray;
	int resource;
	private EditTask editTask;

	public RowTask(Context context, int resource, List<String> objects,List<String> priorityA,List<String> idA) {
		super(context, resource, objects);
		this.context = context;
		myArray = objects;
		priority=priorityA;
		this.resource = resource;
		idArray=idA;
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		
		final int index = position;
		if (rowView == null) {

			LayoutInflater inflater = LayoutInflater.from(context);
			rowView = inflater.inflate(resource, parent, false);
		}
		
		
		final TaskData data  = new TaskData();
		data.txtTitle = (TextView) rowView.findViewById(R.id.text1);// Initializing
		data.btSnooze = (Button) rowView.findViewById(R.id.btSnooze);
		data.btDone = (Button) rowView.findViewById(R.id.btDone);
		data.btCancel = (Button) rowView.findViewById(R.id.btCancel);
		data.linear=(LinearLayout)rowView.findViewById(R.id.mylayout);
		

		String TaskTitle = myArray.get(position);// Get the task name from the
													// array according to
													// position
		data.txtTitle.setText(TaskTitle);// Set the task name in the relevant
											// position of the list
		
		data.btDone.setOnClickListener(new View.OnClickListener() {// Click Done
																	// button

					@Override
					public void onClick(View v) {
						
						AlertDialog.Builder cancel = new AlertDialog.Builder(
								context);// Creating an alert dialog
						cancel.setTitle("Confirm");
						cancel.setMessage("Did you finish the task??");// Asking
																		// the
																		// user
																		// whether
																		// he
																		// finished
																		// the
																		// task
																		// or
																		// not
						cancel.setNegativeButton("No",// The answer is 'no'
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();// Cancelling the dialog
									}
								});
						cancel.setPositiveButton("Yes",// If the answer is 'Yes'
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();// Cancelling the dialog
										editTask.deletePressed(idArray.get(index));// Deleting
																		// the
																		// task
																		// data
																		// from
																		// database

									}
								});
						cancel.create().show();
	
					}
				});


		data.btCancel.setOnClickListener(new View.OnClickListener() {// Cancel
																		// button
																		// clicked

					@Override
					public void onClick(View v) {
						
						AlertDialog.Builder cancel = new AlertDialog.Builder(
								context);
						cancel.setTitle("Confirm");// Asks the user to confirm
													// the action
						cancel.setMessage("Do you wish to delete this task??");
						cancel.setNegativeButton("No",// If the answer 'no'
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();// Cancelling the dialog
									}
								});
						cancel.setPositiveButton("Yes",// If the answer 'yes'
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();// Cancelling the dialog
										editTask.deletePressed(idArray.get(index));// Deleting
																		// the
																		// task
																		// data
																		// from
																		// database
										/*
										 * http://stackoverflow.com/questions/
										 * 17724532
										 * /how-to-remove-listview-item-using
										 * -arrayadapter
										 * -and-notifydatasetchanged Then,
										 * create a method public void
										 * removeFromAdapter(ObjectType
										 * objectToRemove) {
										 * objAdapter.remove(objectToRemove); }
										 * in Helper class and call this method
										 * from your OnClickListener (which is
										 * in HelperAdaptor). Pass Context when
										 * instantiating HelperAdaptor and use
										 * it as
										 * (Helper(myContext)).removeFromAdapter
										 * (objectToRemove). – Vikram Jul 18 '13
										 * at
										 */
									}
								});
						cancel.create().show();// Showing the alert dialog
					}
				});
		data.btSnooze.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if((priority.get(index)).equalsIgnoreCase("high")){
					data.linear.setBackgroundResource(R.drawable.lowrow);
					editTask.updatePriority(idArray.get(index), "low");
				}else{
					data.linear.setBackgroundResource(R.drawable.highrow);
					editTask.updatePriority(idArray.get(index), "high");
				}
				
			}
		});

		data.txtTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Viewing the selected task
				editTask.viewTask(idArray.get(index));
			}
		});

		Log.i("============================================================", priority.get(position));
		if((priority.get(position)).equalsIgnoreCase("high")){
			data.linear.setBackgroundResource(R.drawable.highrow);
		}else{
			data.linear.setBackgroundResource(R.drawable.lowrow);
		}
		
		return rowView;

	}

	static class TaskData {
		TextView txtTitle;
		Button btSnooze;
		Button btDone;
		Button btCancel;
		LinearLayout linear;
	}

	public interface EditTask {
		public void deletePressed(String position);
		public void viewTask(String position);
		public void updatePriority(String position,String priority);
		
	}

	public void setCallback(EditTask callback) {
		editTask = callback;
	}
	
	
}
